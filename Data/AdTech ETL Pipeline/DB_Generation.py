from datetime import datetime, timedelta

import pandas as pd
import numpy as np
import random
def generisi_podatke(nr_rows=100000):
    np.random.seed(42)
    random.seed(42)

    # timestamp
    end_date = datetime.now()
    start_date = end_date - timedelta(days=30)

    hours = np.random.choice(range(24),
                             size=nr_rows,
                             p=[0.02, 0.01, 0.01, 0.01, 0.01, 0.02,
                                0.03, 0.04, 0.05, 0.05, 0.06, 0.05,
                                0.05, 0.05, 0.05, 0.05, 0.04, 0.04,
                                0.06, 0.07, 0.09, 0.07, 0.05, 0.02])

    timestamps = []
    for i in range(nr_rows):
        minutes = np.random.randint(0, 59, nr_rows)
        seconds = np.random.randint(0, 59, nr_rows)
        random_day = np.random.randint(0, 30)
        random_date = start_date + timedelta(days=random_day)
        timestamp = datetime(
            random_date.year, random_date.month, random_date.day,
            hours[i], minutes[i], seconds[i]
        )
        timestamps.append(timestamp)
    user_ids = []
    user_nr = 5000

    user_weights = np.random.exponential(1, user_nr)
    user_weights = user_weights / np.sum(user_weights)

    for _ in range(nr_rows):
        user = np.random.choice(range(1, user_nr + 1), p=user_weights)
        user_ids.append(user)

    devices = np.random.choice(
        ['mobile', 'desktop', 'tablet'],
        size=nr_rows,
        p=[0.65, 0.25, 0.10]
    )

    locations = np.random.choice(
        ['Srbija', 'Hrvatska', 'Bosna i Hercegovina', 'Makedonija', 'Crna Gora', 'Slovenija'],
        size=nr_rows,
        p=[0.60, 0.10, 0.10, 0.05, 0.10, 0.05]
    )

    impressions = np.random.poisson(lam=5, size=nr_rows)
    impressions = np.clip(impressions, 0, 50)

    clicks = []
    for imp in impressions:
        if imp == 0:
            clicks.append(0)
        else:
            click_prob = np.random.uniform(0.01, 0.08)
            click_count = np.random.binomial(imp, click_prob)
            clicks.append(click_count)

    revenue = []
    for click in clicks:
        if click == 0:
            revenue.append(0.0)
        else:
            rev_per_click = np.random.uniform(0.1, 2.0)
            rev = click * rev_per_click
            rev = rev * np.random.uniform(0.8, 1.2)
            revenue.append(round(rev, 2))

    campaign = np.random.choice(
        range(1, 21),
        size=nr_rows,
        p=[0.10, 0.09, 0.08, 0.07, 0.07, 0.06, 0.06, 0.06, 0.05, 0.05,
           0.04, 0.04, 0.04, 0.04, 0.03, 0.03, 0.03, 0.02, 0.02, 0.02]
    )

    publishers = np.random.choice(
        range(1, 101),
        size=nr_rows
    )

    browsers = np.random.choice(
        ['Chrome', 'Safari', 'Firefox', 'Edge', 'Opera', 'Brave'],
        size=nr_rows,
        p=[0.50, 0.20, 0.15, 0.08, 0.04, 0.03]
    )

    df = pd.DataFrame({
        'timestamp': timestamps,
        'user_id': user_ids,
        'device': devices,
        'location': locations,
        'impressions': impressions,
        'clicks': clicks,
        'revenue': revenue,
        'campaign': campaign,
        'publisher': publishers,
        'browser': browsers
    })

    df['ctr'] = (df['clicks'] / df['impressions']).replace([np.inf, -np.inf], 0).fillna(0).round(4)

    df['revenue_per_impressions'] = (df['revenue'] / df['impressions']).replace([np.inf, -np.inf], 0).fillna(0).round(4)

    df['revenue_per_click'] = (df['revenue'] / df['clicks']).replace([np.inf, -np.inf], 0).fillna(0).round(4)

    df['hour'] = pd.to_datetime(df['timestamp']).dt.hour

    df['day_of_the_week'] = pd.to_datetime(df['timestamp']).dt.day_name()

    df['is_weekend'] = df['day_of_the_week'].isin(['Saturday', 'Sunday'])

    df['date'] = pd.to_datetime(df['timestamp']).dt.date

    return df

df = generisi_podatke(100000)

df.to_csv("addtech_data.csv", index=False)