import os
import time

from pyspark.sql import SparkSession
from pyspark.sql.types import StructType, StructField, StringType, IntegerType, DoubleType, TimestampType
from pyspark.sql.functions import broadcast, col, avg, count, sum

spark = SparkSession.builder \
    .appName("Nano_Interactive_Spark_Optimization") \
    .config("spark.sql.adaptive.enabled", "true") \
    .config("spark.sql.adaptive.coalescePartitions.enabled", "true") \
    .config("spark.sql.adaptive.skewJoin.enabled", "true") \
    .config("spark.sql.adaptive.localShuffleReader.enabled", "true") \
    .config("spark.sql.autoBroadcastJoinThreshold", "10MB") \
    .config("spark.memory.fraction", "0.8") \
    .getOrCreate()

print(f'Spark version: {spark.version}')

spark.sparkContext.setLogLevel("WARN")

schema = StructType([
    StructField("timestamp", TimestampType(), True),
    StructField("user_id", IntegerType(), True),
    StructField("device", StringType(), True),
    StructField("location", StringType(), True),
    StructField("impressions", IntegerType(), True),
    StructField("clicks", IntegerType(), True),
    StructField("revenue", DoubleType(), True),
    StructField("campaign", IntegerType(), True),
    StructField("publisher", IntegerType(), True),
    StructField("browser", StringType(), True),
    StructField("ctr", DoubleType(), True),
    StructField("revenue_per_impressions", DoubleType(), True),
    StructField("revenue_per_click", DoubleType(), True),
    StructField("hour", IntegerType(), True),
    StructField("day_of_the_week", StringType(), True),
    StructField("is_weekend", StringType(), True),
    StructField("date", StringType(), True)
])

start_time = time.time()
df = spark.read \
    .option("header", "true") \
    .schema(schema) \
    .csv("../Project1_adtech/addtech_data.csv")

row_count = df.count()
load_time = time.time() - start_time

print(f"Ucitano {df.count()} redova")
df.printSchema()

print("\nPrvih 5 redova")
df.select("timestamp", "device", "impressions", "clicks", "revenue").show(5, truncate=False)

# Caching (Kesiranje)

df.cache()
df.count()


def measure_performance(query_name, df_operation):
    # prvo izvrsavanje(bez kesa)
    start = time.time()
    result = df_operation()
    first_time = time.time() - start

    # drugo izvrsavanje(sa kesom)
    start = time.time()
    result = df_operation()
    second_time = time.time() - start

    print(f"Query {query_name} took {first_time} seconds")
    print(f"Query {query_name} took {second_time} seconds")
    print(f"Ubrzanje od {first_time / second_time:.1f}x")

    return result


measure_performance(
    "Group by device",
    lambda: df.groupBy("device").count().collect()
)

# Optimizacija

# sifarnik kampanja
campaign_dim = spark.createDataFrame([
    (1, "Letnja Kampanja 2025", "Branding", 50000),
    (2, "Zimska Kampanja 2025", "Performance", 75000),
    (3, "Prolećna kampanja 2026", "Branding", 60000),
    (4, "Back to school", "Performance", 45000),
    (5, "Black Friday", "Sales", 120000),
    (6, "Novogodišnja", "Branding", 80000),
    (7, "Sportski događaji", "Performance", 55000),
    (8, "Tech conference", "Branding", 70000),
    (9, "Travel deals", "Sales", 65000),
    (10, "Lokalne kampanje", "Performance", 40000)
], ["campaign_id", "campaign_name", "campaign_type", "budget"])

print(f"sifarnik ima {campaign_dim.count()} redova")

# regular join (bez optimizacije)

start = time.time()
regular_join = df.join(campaign_dim, df.campaign == campaign_dim.campaign_id)
regular_join_count = regular_join.count()
regular_time = time.time() - start

print(f"Vreme: {regular_time:.3f}s")

# Broadcast join (sa optimizacijom)

start = time.time()
broadcast_join = df.join(broadcast(campaign_dim), df.campaign == campaign_dim.campaign_id)
broadcast_join_count = broadcast_join.count()
broadcast_time = time.time() - start

print(f"Broadcast vreme: {broadcast_time:.3f}s")
print(f"Ubrzanje: {regular_time / broadcast_time:.1f}x")

print("\nprimer podataka sa imenima kampanja:")
broadcast_join.select("timestamp", "device", "campaign_name", "campaign_type", "revenue") \
    .orderBy(col("revenue").desc()).limit(5).show(truncate=False)

# Particionisanje i optimizacija

print(f"Pocetni broj particija: {df.rdd.getNumPartitions()}")

print("Reparticiosanje po 'device' koloni")
df_repartitioned = df.repartition(4, "device")
print(f"Novi broj particija: {df_repartitioned.rdd.getNumPartitions()}")

print("\nKoalesce - smanjivanje broj particija")
df_coalsced = df.coalesce(2)
print(f"Broj particija posle coalesce: {df_coalsced.rdd.getNumPartitions()}")

# Spark SQL i fizicki plan

df.createOrReplaceTempView("adtech")

sql_query = """
            SELECT device,
                   campaign,
                   SUM(revenue) as total_revenue,
                   AVG(ctr) as avg_ctr,
        COUNT(DISTINCT user_id) as unique_users
            FROM adtech
            WHERE revenue > 0
            GROUP BY device, campaign
            HAVING total_revenue > 100
            ORDER BY total_revenue DESC
                LIMIT 20 \
            """

result = spark.sql(sql_query)
print("Izvrsen sql upit")

print("Fizicki plan izvrsenja")
result.explain("extended")

print("\nRezultat upisa")
result.show(truncate=False)

# Formati podataka (CSV vs Parquet)

device_agg = df.groupBy("device").agg(
    sum("revenue").alias("total_revenue"),
    avg("ctr").alias("avg_ctr"),
    count("user_id").alias("total_events")
)

print("Cuvanje kao CSV")
start = time.time()
device_agg.write.mode("overwrite").option("header", "true").csv("output.csv")
csv_time = time.time() - start

print("Cuvanje kao Parquet")
start = time.time()
device_agg.write.mode("overwrite").parquet("output_parquet")
parquet_time = time.time() - start


def get_folder_size(path):
    total = 0
    for root, dirs, files in os.walk(path):
        for file in files:
            if file.startswith("part-"):
                total += os.path.getsize(os.path.join(root, file))
    return total


csv_size = get_folder_size("output.csv")
parquet_size = get_folder_size("output_parquet")

print("Poredjenje formata")
print(f"CSV vreme cuvanja: {csv_time:.3f}s")
print(f"Parquet vreme cuvanja: {parquet_time:.3f}s")
print(f"CSV velicina: {csv_size / 1024:.2f}KB")
print(f"Parquet velicina: {parquet_size / 1024:.2f}KB")
print(f"Usteda u prostoru {(1 - parquet_size / csv_size) * 100:.1f}%")

spark.stop()
