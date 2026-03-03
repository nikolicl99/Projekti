from datetime import datetime
from yattag import Doc, indent

import numpy as np
import pandas as pd
import sqlite3
import matplotlib.pyplot as plt
import seaborn as sns
import stats

conn = sqlite3.connect("addtech_data.csv")


def transform_data(df):
    # Ova funkcija vrsi statisticku analizu podataka
    # Racuna deskriptivnu statistiku, outliere, korelaciju i agregacije

    print("\nDeskriptivna statistika")
    # Prikazuje osnovne statisticke parametre za svaku kolonu
    print(df[['impressions', 'clicks', 'revenue', 'ctr']].describe())

    # Outliers - detekcija vrednosti koje odstupaju od normale
    # Koristi se IQR metod (interkvartilni razmak)
    Q1 = df['revenue'].quantile(0.25)  # Prvi kvartil (25%)
    Q3 = df['revenue'].quantile(0.75)  # Treci kvartil (75%)
    IQR = Q3 - Q1  # Interkvartilni razmak
    # Outlier-i su vrednosti manje od Q1-1.5*IQR ili vece od Q3+1.5*IQR
    outliers = df[(df['revenue'] < Q1 - 1.5 * IQR) | (df['revenue'] > Q3 + 1.5 * IQR)]
    print(f"\nOutlier-i u revenue: {len(outliers)} ({len(outliers) / len(df) * 100:.2f}%)")

    # Korelacija - meri povezanost izmedju razlicitih metrika
    print("\nKorelacija metrika")
    corr = df[['impressions', 'clicks', 'revenue', 'ctr']].corr()
    print(corr)

    # Agregacija - grupisemo podatke po razlicitim kategorijama

    # Agregacija po uredjajima (mobile, desktop, tablet)
    device_stats = df.groupby('device').agg({
        'impressions': 'sum',  # Ukupno impresija
        'clicks': 'sum',  # Ukupno klikova
        'revenue': 'sum',  # Ukupna zarada
        'user_id': 'nunique',  # Broj jedinstvenih korisnika
        'ctr': 'mean',  # Prosecan CTR
    }).round(2)
    # Racunamo CTR u procentima za svaki uredjaj
    device_stats['ctr_pct'] = (device_stats['clicks'] / device_stats['impressions'] * 100).round(2)
    print("\nAgregacija po uredjajima")
    print(device_stats)

    # Dnevna agregacija - podaci grupisani po datumima
    daily_stats = df.groupby('date').agg({
        'revenue': 'sum',
        'clicks': 'sum',
        'impressions': 'sum',
        'user_id': 'nunique'
    }).reset_index()
    # Racunamo dnevni CTR u procentima
    daily_stats['ctr'] = (daily_stats['clicks'] / daily_stats['impressions'] * 100).round(2)

    # Agregacija po kampanjama
    campaign_stats = df.groupby('campaign').agg({
        'revenue': 'sum',
        'clicks': 'sum'
    }).round(2)

    # Svi rezultati se smestaju u recnik koji funkcija vraca
    stats = {
        'device_stats': device_stats,
        'campaign_stats': campaign_stats,
        'daily_stats': daily_stats,
        'correlation': corr
    }

    return stats


def create_visualisation(df, stats):
    # Ova funkcija kreira cetiri grafikona za vizuelni prikaz podataka
    # Pravimo figuru sa 2x2 subplota
    fig, axes = plt.subplots(2, 2, figsize=(15, 10))

    # Prvi grafikon - prosecan CTR po tipu uredjaja (bar chart)
    device_ctr = df.groupby('device')['ctr'].mean()
    axes[0, 0].bar(device_ctr.index, device_ctr.values, color=['blue', 'green', 'orange'])
    axes[0, 0].set_title('Prosecan CTR po Uredjaju')
    axes[0, 0].set_ylabel('CTR')
    axes[0, 0].grid(axis='y', alpha=0.3)

    # Drugi grafikon - ukupan revenue po satu (line chart)
    hourly_revenue = df.groupby('hour')['revenue'].sum()
    axes[0, 1].plot(hourly_revenue.index, hourly_revenue.values, marker='o', color='red')
    axes[0, 1].set_title('Ukupan Revenue po satu')
    axes[0, 1].set_ylabel('Revenue')
    axes[0, 1].set_xlabel('Sat')
    axes[0, 1].grid(alpha=0.3)

    # Treci grafikon - distribucija CTR vrednosti (histogram)
    axes[1, 0].hist(df[df['ctr'] > 0]['ctr'], bins=50, color='purple', alpha=0.7)
    axes[1, 0].set_title('Distribucija CTR-a')
    axes[1, 0].set_xlabel('CTR')
    axes[1, 0].set_ylabel('Frekvencija')

    # Cetvrti grafikon - korelaciona matrica (heatmap)
    sns.heatmap(stats['correlation'], annot=True, cmap='coolwarm', center=0, ax=axes[1, 1])
    axes[1, 1].set_title('Korelaciona matrica')

    # Podesavamo razmake i cuvamo grafikon kao sliku
    plt.tight_layout()
    plt.savefig('adtech_analiza.png', dpi=100, bbox_inches='tight')
    return fig


def load_data(df, stats, db_path="adtech_analiza.db"):
    # Ova funkcija cuva podatke u SQLite bazu podataka
    # Kreiramo konekciju ka bazi
    conn = sqlite3.connect(db_path)

    # Sirovi podaci se cuvaju u tabelu raw_events
    df.to_sql('raw_events', conn, if_exists='replace', index=False)

    # Dnevni agregati se cuvaju u tabelu daily_metrics
    stats['daily_stats'].to_sql('daily_metrics', conn, if_exists='replace', index=False)

    # Performanse po uredjajima se cuvaju u tabelu device_performance
    # reset_index pretvara 'device' iz indeksa u obicnu kolonu
    stats['device_stats'].reset_index().to_sql('device_performance', conn, if_exists='replace', index=False)

    # Performanse po kampanjama se cuvaju u tabelu campaign_performance
    stats['campaign_stats'].reset_index().to_sql('campaign_performance', conn, if_exists='replace', index=False)

    # Zatvaramo konekciju
    conn.close()


def generate_html_report(df, stats, output_file='adtech_report.html'):
    # Ova funkcija generise HTML izvestaj sa svim rezultatima
    # Koristi Yattag biblioteku za kreiranje HTML strukture
    doc, tag, text = Doc().tagtext()

    # Izracunavamo osnovne metrike za izvestaj
    total_revenue = df['revenue'].sum()
    avg_ctr = (df['clicks'].sum() / df['impressions'].sum() * 100).round(2)
    unique_users = df['user_id'].nunique()

    # Racunanje Pareto principa - top 20% korisnika po zaradi
    # Grupisemo revenue po korisnicima i sortiramo opadajuce
    user_revenue = df.groupby('user_id')['revenue'].sum().sort_values(ascending=False)
    # Odredjujemo koliko korisnika cini 20% (minimum 1)
    top_20_count = max(1, int(len(user_revenue) * 0.2))
    # Uzimamo prvih 20% korisnika
    top_20_users = user_revenue.head(top_20_count)
    # Racunamo njihov ukupan revenue i procenat u odnosu na ukupan
    top_20_revenue = top_20_users.sum()
    top_20_percent = (top_20_revenue / total_revenue) * 100
    # Uzimamo korelaciju izmedju impresija i klikova
    correlation = stats['correlation'].loc['impressions', 'clicks']

    # Kreiramo HTML strukturu
    with tag('html'):
        with tag('head'):
            with tag('style'):
                # CSS stilovi za izgled izvestaja
                text("""
                                    body { font-family: Arial, sans-serif; margin: 40px; background: #f5f5f5; }
                                    h1 { color: #2c3e50; border-bottom: 3px solid #3498db; padding-bottom: 10px; }
                                    h2 { color: #34495e; margin-top: 30px; }
                                    .summary { background: white; padding: 20px; border-radius: 10px; box-shadow: 0 2px 5px rgba(0,0,0,0.1); }
                                    .metric-container { display: flex; flex-wrap: wrap; gap: 20px; margin: 20px 0; }
                                    .metric { flex: 1; min-width: 150px; padding: 20px; background: #3498db; color: white; border-radius: 5px; text-align: center; }
                                    .metric span { font-size: 24px; font-weight: bold; display: block; margin-top: 10px; }
                                    table { border-collapse: collapse; width: 100%; background: white; border-radius: 10px; overflow: hidden; margin: 20px 0; }
                                    th { background: #3498db; color: white; padding: 12px; text-align: left; }
                                    td { padding: 10px; border-bottom: 1px solid #ddd; }
                                    tr:hover { background: #f5f5f5; }
                                    img { width: 100%; max-width: 800px; margin: 20px auto; display: block; border-radius: 10px; box-shadow: 0 2px 5px rgba(0,0,0,0.2); }
                                    .insight { background: #e8f4f8; padding: 20px; border-left: 5px solid #3498db; margin: 20px 0; border-radius: 0 5px 5px 0; }
                                    .footer { text-align: center; margin-top: 50px; color: #7f8c8d; }
                                """)

        with tag('body'):
            # Naslov izvestaja
            with tag('h1'):
                text('AdTech Data Analyst Report')

            # Datum i vreme generisanja
            with tag('p'):
                text(f'Generated: {datetime.now().strftime("%Y-%m-%d %H:%M:%S")}')

            # Sekcija sa kljucnim metrikama
            with tag('div', klass='summary'):
                with tag('h2'):
                    text('Kljucna metrika')

                with tag('div', klass='metric_container'):
                    # Ukupno dogadjaja
                    with tag('div', klass='metric'):
                        text('Ukupno dogadjaja')
                        with tag('span'):
                            text(f'${total_revenue:,.2f}')

                    # Ukupan revenue
                    with tag('div', klass='metric'):
                        text('Ukupan revenue')
                        with tag('span'):
                            text(f'${total_revenue:,.2f}')

                    # Prosecan CTR
                    with tag('div', klass='metric'):
                        text('Prosecan CTR')
                        with tag('span'):
                            text(f'${avg_ctr:.2f}')

                    # Broj jedinstvenih korisnika
                    with tag('div', klass='metric'):
                        text('Jedinstvenih korisnika')
                        with tag('span'):
                            text(f'${unique_users:,}')

            # Tabela performansi po uredjaju
            with tag('h2'):
                text('Performanse po uredjaju')

            doc.asis(stats['device_stats'].to_html())

            # Tabela top 5 kampanja
            with tag('h2'):
                text('Top 5 kampanja')

            top_campaigns = stats['campaign_stats'].nlargest(5, 'revenue')
            doc.asis(top_campaigns.to_html())

            # Vizuelna analiza - slika sa grafikonima
            with tag('h2'):
                text('Vizuelna analiza')

            with tag('img', src='adtech_analiza.png', alt='Analiza grafikoni'):
                pass

            # Sekcija sa kljucnim uvidima
            with tag('div', klass='insight'):
                with tag('h3'):
                    text('Kljucni uvidi:')

                with tag('ul'):
                    # Uvid o uredjajima
                    with tag('li'):
                        with tag('b'):
                            text('mobile')
                        text(' dominira po broju dogadjaja, ali ')
                        with tag('b'):
                            text('desktop')
                        text(' ima bolji CTR')

                    # Uvid o vremenskim obrascima
                    with tag('li'):
                        text('Najveci revenue je u periodu ')
                        with tag('b'):
                            text('20-23h')

                    # Uvid o Pareto principu
                    with tag('li'):
                        text(f'Top 20% korisnika donosi {top_20_percent:.1f}% ukupnog revenue')

                    # Uvid o korelaciji
                    with tag('li'):
                        text(f'Korelacija izmedju impresija i klikova: {correlation:.2f}')

            # Footer
            with tag('p', klass='footer'):
                text('Izvestaj generisan automatski kroz ETL pipeline')

    # Indentiramo HTML i cuvamo u fajl
    html = indent(doc.getvalue())
    with open(output_file, 'w', encoding='utf8') as f:
        f.write(html)


# Glavni deo programa - pokrece ceo pipeline
if __name__ == "__main__":
    # Ucitavamo podatke iz CSV fajla
    df = pd.read_csv("addtech_data.csv", parse_dates=['timestamp'])
    # Primenjujemo transformacije i analizu
    stats = transform_data(df)
    # Kreiramo vizuelizacije
    create_visualisation(df, stats)
    # Cuvamo podatke u bazu
    load_data(df, stats)
    # Generisemo HTML izvestaj
    generate_html_report(df, stats)