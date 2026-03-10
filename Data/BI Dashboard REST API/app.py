from flask import Flask, jsonify, request
from datetime import datetime
from yattag import Doc
import pandas as pd
import os

app = Flask(__name__)


def get_db_connection():
    if not os.path.exists('data/addtech_data.csv'):
        return None
    df = pd.read_csv('data/addtech_data.csv', parse_dates=['timestamp'])
    return df


def json_serializer(obj):
    if isinstance(obj, (datetime, pd.Timestamp)):
        return obj.strftime("%Y-%m-%d %H:%M:%S")
    raise TypeError(f"Type: {type(obj)} not serializable")


@app.route('/')
def home():
    doc, tag, text = Doc().tagtext()

    doc.asis('<!DOCTYPE html>')
    with tag('html'):
        with tag('head'):
            with tag('title'):
                text('BI Dashboard - Launcher')
            with tag('style'):
                text('''
                                   body { 
                                       font-family: Arial, sans-serif; 
                                       max-width: 800px; 
                                       margin: 50px auto; 
                                       padding: 20px;
                                       background: #f5f5f5;
                                   }
                                   h1 { color: #333; }
                                   .endpoint-list {
                                       background: white;
                                       border-radius: 10px;
                                       padding: 20px;
                                       box-shadow: 0 2px 5px rgba(0,0,0,0.1);
                                   }
                                   .endpoint-item {
                                       padding: 15px;
                                       border-bottom: 1px solid #eee;
                                       display: flex;
                                       align-items: center;
                                   }
                                   .endpoint-item:last-child { border-bottom: none; }
                                   .method {
                                       background: #667eea;
                                       color: white;
                                       padding: 3px 8px;
                                       border-radius: 3px;
                                       font-size: 12px;
                                       margin-right: 15px;
                                       min-width: 40px;
                                       text-align: center;
                                   }
                                   .method-post {
                                       background: #f59e0b;
                                       color: white;
                                       padding: 3px 8px;
                                       border-radius: 3px;
                                       font-size: 12px;
                                       margin-right: 15px;
                                       min-width: 40px;
                                       text-align: center;
                                   }
                                   .path {
                                       flex: 1;
                                       font-family: monospace;
                                       color: #555;
                                   }
                                   .desc {
                                       color: #777;
                                       margin-right: 20px;
                                       font-size: 14px;
                                   }
                                   .btn {
                                       background: #48bb78;
                                       color: white;
                                       padding: 8px 15px;
                                       text-decoration: none;
                                       border-radius: 5px;
                                       font-size: 14px;
                                   }
                                   .btn:hover { background: #38a169; }
                                   .btn.api { background: #667eea; }
                                   .btn.api:hover { background: #5a67d8; }
                                   .btn.post-btn { background: #f59e0b; }
                                   .btn.post-btn:hover { background: #d97706; }
                                   .post-form {
                                       display: flex;
                                       align-items: center;
                                       gap: 10px;
                                   }
                                   select {
                                       padding: 6px;
                                       border-radius: 4px;
                                       border: 1px solid #ddd;
                                       font-size: 13px;
                                   }
                               ''')
        with tag('body'):
            with tag('h1'): text('BI Dashboard - Brzi pristup reportima')
            with tag('div', klass='endpoint-list'):
                endpoints = [
                    ('GET', '/api/metrics/daily?days=7', 'Dnevni agregati (7 dana)'),
                    ('GET', '/api/metrics/daily?days=30', 'Dnevni agregati (30 dana)'),
                    ('GET', '/api/metrics/realtime', 'Real-time (danas)'),
                    ('GET', '/api/analytics/device', 'Performanse po uređaju'),
                    ('GET', '/api/analytics/campaign/top?limit=5', 'Top 5 kampanja'),
                    ('GET', '/api/analytics/campaign/top?limit=10', 'Top 10 kampanja'),
                    ('GET', '/api/analytics/funnel', 'Funnel analiza'),
                    ('GET', '/api/analytics/pareto', 'Pareto analiza'),
                    ('GET', '/api/dashboard/export', 'Kompletan export'),
                ]

                for method, path, desc in endpoints:
                    with tag('div', klass='endpoint-item'):
                        with tag('span', klass='method'): text(method)
                        with tag('span', klass='desc'): text(desc)
                        with tag('span', klass='path'): text(path)
                        with tag('a', href=path, target='_blank', klass='btn api'):
                            text('Otvori u novom tabu')

                with tag('div', klass='endpoint-item'):
                    with tag('span', klass='method-post'): text('POST')
                    with tag('span', klass='desc'): text('Tracking Pixel')
                    with tag('span', klass='path'): text('/api/events')

                    with tag('form', method='POST', action='/api/events', target='_blank', klass='post-form'):
                        with tag('select', name='event_type'):
                            with tag('option', value='page_view'): text('Page View')
                            with tag('option', value='click'): text('Click')
                            with tag('option', value='purchase'): text('Purchase')

                        with tag('input', type='hidden', name='user_id', value='test_user'):
                            pass
                        with tag('input', type='hidden', name='device', value='desktop'):
                            pass

                        with tag('button', type='submit', klass='btn post-btn'):
                            text('Posalji')
    return doc.getvalue()


@app.route('/api/metrics/daily', methods=['GET'])
def daily_metrics():
    try:
        df = get_db_connection()
        if df is None:
            return jsonify({"error": "Podaci nisu dostupni"}), 500

        days = request.args.get('days', default=30, type=int)
        latest_date = df['date'].max()
        cutoff_date = pd.to_datetime(latest_date) - pd.Timedelta(days=days)
        df_filtered = df[pd.to_datetime(df['date']) >= cutoff_date]

        daily = df_filtered.groupby('date').agg({
            'revenue': 'sum',
            'clicks': 'sum',
            'impressions': 'sum',
            'user_id': 'nunique'
        }).reset_index()

        daily['ctr'] = (daily['clicks'] / daily['impressions'] * 100).round(2)

        return jsonify({
            "status": "success",
            "days": days,
            "data": daily.to_dict(orient='records')
        })

    except Exception as e:
        return jsonify({"error": str(e)}), 500


@app.route('/api/metrics/realtime', methods=['GET'])
def realtime_metrics():
    try:
        df = get_db_connection()
        if df is None:
            return jsonify({"error": "Podaci nisu dostupni"}), 500

        today = datetime.now().strftime("%Y-%m-%d")
        df_today = df[df['date'] == today]

        if len(df_today) == 0:
            last_date = df['date'].max()
            df_today = df[df['date'] == last_date]
            today = last_date

        hourly = df_today.groupby('hour').agg({
            'revenue': 'sum',
            'clicks': 'sum',
            'impressions': 'sum',
            'user_id': 'nunique'
        }).reset_index()

        hourly['ctr'] = (hourly['clicks'] / hourly['impressions'] * 100).round(2)

        return jsonify({
            "status": "success",
            "days": today,
            "data": hourly.to_dict(orient='records')
        })

    except Exception as e:
        return jsonify({"error": str(e)}), 500


@app.route('/api/analytics/device', methods=['GET'])
def device_performance():
    try:
        df = get_db_connection()
        if df is None:
            return jsonify({"error": "Podaci nisu dostupni"}), 500

        device_stats = df.groupby('device').agg({
            'impressions': 'sum',
            'clicks': 'sum',
            'revenue': 'sum',
            'user_id': 'nunique',
            'ctr': 'mean',
        }).round(2)

        device_stats['ctr_pct'] = (device_stats['clicks'] / device_stats['impressions'] * 100).round(2)

        device_stats = device_stats.reset_index()
        return jsonify({
            "status": "success",
            "data": device_stats.to_dict(orient='records')
        })

    except Exception as e:
        return jsonify({"error": str(e)}), 500


@app.route('/api/analytics/campaign/top', methods=['GET'])
def campaign_top():
    try:
        df = get_db_connection()
        if df is None:
            return jsonify({"error": "Podaci nisu dostupni"}), 500

        limit = request.args.get('limit', default=5, type=int)

        campaign_stats = df.groupby('campaign').agg({
            'revenue': 'sum',
            'clicks': 'sum',
            'impressions': 'sum'
        }).round(2)

        campaign_stats['ctr'] = (campaign_stats['clicks'] / campaign_stats['impressions'] * 100).round(2)

        top_campaigns = campaign_stats.nlargest(limit, 'revenue').reset_index()

        return jsonify({
            "status": "success",
            "limit": limit,
            "data": top_campaigns.to_dict(orient='records')
        })

    except Exception as e:
        return jsonify({"error": str(e)}), 500


@app.route('/api/analytics/funnel', methods=['GET'])
def funnel_analysis():
    try:
        df = get_db_connection()
        if df is None:
            return jsonify({"error": "Podaci nisu dostupni"}), 500

        total_impressions = df['impressions'].sum()
        total_clicks = df['clicks'].sum()
        total_revenue = df['revenue'].sum()

        users_with_impressions = df['user_id'].nunique()
        users_with_clicks = df[df['clicks'] > 0]['user_id'].nunique()
        users_with_revenue = df[df['revenue'] > 0]['user_id'].nunique()

        funnel_data = [
            {
                "stage": "impressions",
                "count": int(total_impressions),
                "unique_users": int(users_with_impressions),
                "conversion_rates": 100.0
            },
            {
                "stage": "clicks",
                "count": int(total_clicks),
                "unique_users": int(users_with_clicks),
                "conversion_rates": round(total_clicks / total_impressions * 100, 2)
            },
            {
                "stage": "revenue",
                "count": float(total_revenue),
                "unique_users": int(users_with_revenue),
                "conversion_rates": round(users_with_revenue / users_with_impressions * 100, 2)
            }
        ]

        return jsonify({
            "status": "success",
            "funnel": funnel_data
        })

    except Exception as e:
        return jsonify({"error": str(e)}), 500


@app.route('/api/analytics/pareto', methods=['GET'])
def pareto_analysis():
    try:
        df = get_db_connection()
        if df is None:
            return jsonify({"error": "Podaci nisu dostupni"}), 500

        user_revenue = df.groupby('user_id')['revenue'].sum().sort_values(ascending=False)
        total_revenue = user_revenue.sum()
        total_users = len(user_revenue)
        top_20_count = max(1, int(total_users * 0.2))
        top_20_users = user_revenue.head(top_20_count)
        top_20_revenue = top_20_users.sum()
        top_20_percent = round(top_20_revenue / total_revenue * 100, 2)

        return jsonify({
            "status": "success",
            "total_users": total_users,
            "total_revenue": float(total_revenue),
            "pareto": {
                "top_20_percent_users": top_20_count,
                "top_20_percent_revenue": float(top_20_revenue),
                "top_20_percent_share": top_20_percent
            }
        })

    except Exception as e:
        return jsonify({"error": str(e)}), 500


@app.route('/api/events', methods=['POST'])
def track_events():
    try:
        if request.is_json:
            events = request.json
        else:
            events = request.form.to_dict()
        required_fields = ['event_type', 'user_id', 'device']
        for field in required_fields:
            if field not in events:
                return jsonify({"error": f"Missing filed: {field}"}), 400

        if 'timestamp' not in events:
            events['timestamp'] = datetime.now().isoformat()

        print(f"Primljen dogadjaj {events}")

        return jsonify({
            "status": "success",
            "message": "Event tracked successfully",
            "received_at": datetime.now().isoformat()
        }), 201

    except Exception as e:
        return jsonify({"error": str(e)}), 500


@app.route('/api/dashboard/export', methods=['GET'])
def export_dashboard():
    try:
        df = get_db_connection()
        if df is None:
            return jsonify({"error": "Podaci nisu dostupni"}), 500

        total_revenue = float(df['revenue'].sum())
        total_clicks = int(df['clicks'].sum())
        total_impressions = int(df['impressions'].sum())
        avg_ctr = round(total_clicks / total_impressions * 100, 2)
        unique_users = int(df['user_id'].nunique())

        device_stats = df.groupby('device').agg({
            'revenue': 'sum',
            'clicks': 'sum',
            'user_id': 'nunique'
        }).reset_index()
        device_stats['revenue'] = device_stats['revenue'].round(2)

        return jsonify({
            "status": "success",
            "exported_at": datetime.now().isoformat(),
            "summary": {
                "total_revenue": total_revenue,
                "total_clicks": total_clicks,
                "total_impressions": total_impressions,
                "avg_ctr": avg_ctr,
                "unique_users": unique_users
            },
            "device_performance": device_stats.to_dict(orient='records')
        })

    except Exception as e:
        return jsonify({"error": str(e)}), 500


if __name__ == '__main__':
    print("=" * 60)
    print("🚀 BI Dashboard REST API")
    print("=" * 60)
    print("\nDostupni endpoint-i:")
    print("  GET    /                          - Dokumentacija")
    print("  GET    /api/metrics/daily          - Dnevni agregati")
    print("  GET    /api/metrics/realtime       - Real-time metrike")
    print("  GET    /api/analytics/device       - Performanse po uređaju")
    print("  GET    /api/analytics/campaign/top - Top kampanje")
    print("  GET    /api/analytics/funnel       - Funnel analiza")
    print("  GET    /api/analytics/pareto       - Pareto analiza")
    print("  POST   /api/events                  - Tracking pixel")
    print("  GET    /api/dashboard/export       - Kompletan export")
    print("\n📌 Server pokrenut na: http://localhost:5000")
    print("📌 Pritisni Ctrl+C za gašenje")
    print("=" * 60)
    app.run(debug=True, port=5000)
