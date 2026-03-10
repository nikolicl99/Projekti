import os
import time

from pyspark.sql import SparkSession
from pyspark.sql.types import StructType, StructField, StringType, IntegerType, DoubleType, TimestampType
from pyspark.sql.functions import broadcast, col, avg, count, sum

# ============================================
# 1. INICIJALIZACIJA SPARK SESIJE SA OPTIMIZACIJAMA
# ============================================
# SparkSession je glavni ulaz u Spark - preko njega ucitavamo podatke,
# kreiramo DataFrame-ove i izvrsavamo upite.
spark = SparkSession.builder \
    .appName("Nano_Interactive_Spark_Optimization") \
    .config("spark.sql.adaptive.enabled", "true") \
    .config("spark.sql.adaptive.coalescePartitions.enabled", "true") \
    .config("spark.sql.adaptive.skewJoin.enabled", "true") \
    .config("spark.sql.adaptive.localShuffleReader.enabled", "true") \
    .config("spark.sql.autoBroadcastJoinThreshold", "10MB") \
    .config("spark.memory.fraction", "0.8") \
    .config("spark.network.timeout", "600s") \
    .config("spark.executor.heartbeatInterval", "60s") \
    .config("spark.sql.broadcastTimeout", "1200") \
    .config("spark.sql.shuffle.partitions", "4") \
    .getOrCreate()

print(f'Spark verzija: {spark.version}')

# Smanjujemo nivo logovanja da ne bismo dobijali previse poruka
spark.sparkContext.setLogLevel("WARN")

# ============================================
# 2. DEFINISANJE SEME PODATAKA
# ============================================
# Eksplicitno definisemo semu umesto da Spark pogadja tipove.
# Ovo je kao @Entity u Javi - definise strukturu podataka.
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

# ============================================
# 3. UCITAVANJE PODATAKA (EXTRACT)
# ============================================
start_time = time.time()
df = spark.read \
    .option("header", "true") \
    .schema(schema) \
    .csv("../AdTech ETL Pipeline/addtech_data.csv")

row_count = df.count()
load_time = time.time() - start_time

print(f"Ucitano {df.count()} redova")
df.printSchema()

print("\nPrvih 5 redova")
df.select("timestamp", "device", "impressions", "clicks", "revenue").show(5, truncate=False)

# ============================================
# 4. KESIRANJE (CACHING) - OPTIMIZACIJA 1
# ============================================
# Kesiranje cuva DataFrame u memoriji za brzi pristup.
# Ovo je korisno kada se isti podaci koriste vise puta.
df.cache()
df.count()


def measure_performance(query_name, df_operation):
    """
    Meri performanse Spark operacije prvi i drugi put.
    Pokazuje koliko kesiranje ubrzava izvrsenje.
    """
    # prvo izvrsavanje (bez kesa)
    start = time.time()
    result = df_operation()
    first_time = time.time() - start

    # drugo izvrsavanje (sa kesom)
    start = time.time()
    result = df_operation()
    second_time = time.time() - start

    print(f"Operacija {query_name} trajala {first_time:.3f} sekundi")
    print(f"Operacija {query_name} trajala {second_time:.3f} sekundi (sa kesom)")
    if second_time > 0:
        print(f"Ubrzanje: {first_time / second_time:.1f}x")
    else:
        print("Ubrzanje: nije merljivo")

    return result


# Testiramo kesiranje na jednostavnom grupisanju
measure_performance(
    "Group by device",
    lambda: df.groupBy("device").count().collect()
)

# ============================================
# 5. BROADCAST JOIN - OPTIMIZACIJA 2
# ============================================
# Kreiramo malu dimenzionu tabelu (sifarnik kampanja)
# Ovo je kao pomocna tabela u bazi podataka.
campaign_dim = spark.createDataFrame([
    (1, "Letnja Kampanja 2025", "Branding", 50000),
    (2, "Zimska Kampanja 2025", "Performance", 75000),
    (3, "Prolecna kampanja 2026", "Branding", 60000),
    (4, "Back to school", "Performance", 45000),
    (5, "Black Friday", "Sales", 120000),
    (6, "Novogodisnja", "Branding", 80000),
    (7, "Sportski dogadjaji", "Performance", 55000),
    (8, "Tech conference", "Branding", 70000),
    (9, "Travel deals", "Sales", 65000),
    (10, "Lokalne kampanje", "Performance", 40000)
], ["campaign_id", "campaign_name", "campaign_type", "budget"])

print(f"Sifarnik ima {campaign_dim.count()} redova")

# Regular join (bez optimizacije) - moze izazvati shuffle (spor prenos podataka)
start = time.time()
regular_join = df.join(campaign_dim, df.campaign == campaign_dim.campaign_id)
regular_join_count = regular_join.count()
regular_time = time.time() - start
print(f"Regular join vreme: {regular_time:.3f}s")

# Broadcast join (sa optimizacijom) - salje malu tabelu na SVE cvorove
# Ovo eliminishe shuffle i mnogo je brze za male tabele
start = time.time()
broadcast_join = df.join(broadcast(campaign_dim), df.campaign == campaign_dim.campaign_id)
broadcast_join_count = broadcast_join.count()
broadcast_time = time.time() - start

print(f"Broadcast join vreme: {broadcast_time:.3f}s")
if broadcast_time > 0:
    print(f"Ubrzanje: {regular_time / broadcast_time:.1f}x")

print("Napomena: Broadcast join nije dao veliko ubrzanje jer je sifarnik mali (10 redova).")
print("Na 1GB+ podataka, ubrzanje bi bilo 10-100x vece jer se izbegava shuffle.")

print("\nPrimer podataka sa imenima kampanja:")
broadcast_join.select("timestamp", "device", "campaign_name", "campaign_type", "revenue") \
    .orderBy(col("revenue").desc()).limit(5).show(truncate=False)

# ============================================
# 6. PARTICIONISANJE - OPTIMIZACIJA 3
# ============================================
# Particionisanje odredjuje kako su podaci rasporedjeni po cvorovima.
print(f"Pocetni broj particija: {df.rdd.getNumPartitions()}")

# repartition - povecava broj particija (izaziva shuffle)
print("Reparticionisanje po 'device' koloni...")
df_repartitioned = df.repartition(4, "device")
print(f"Novi broj particija posle repartition: {df_repartitioned.rdd.getNumPartitions()}")

# coalesce - smanjuje broj particija (NE izaziva shuffle, bezbednije)
print("\nKoalesce - smanjivanje broja particija...")
df_coalesced = df.coalesce(2)
print(f"Broj particija posle coalesce: {df_coalesced.rdd.getNumPartitions()}")

# ============================================
# 7. SPARK SQL I FIZICKI PLAN
# ============================================
# Spark omogucava pisanje SQL upita direktno nad DataFrame-om
df.createOrReplaceTempView("adtech")

sql_query = """
    SELECT device,
           campaign,
           SUM(revenue)            as total_revenue,
           AVG(ctr)                as avg_ctr,
           COUNT(DISTINCT user_id) as unique_users
    FROM adtech
    WHERE revenue > 0
    GROUP BY device, campaign
    HAVING total_revenue > 100
    ORDER BY total_revenue DESC 
    LIMIT 20
"""

result = spark.sql(sql_query)
print("Izvršen SQL upit")

# explain("extended") prikazuje kako ce Spark izvrsiti upit
# Parsed Logical Plan -> Analyzed -> Optimized -> Physical Plan
print("\n" + "="*70)
print("FIZICKI PLAN IZVRŠENJA - kljuc za razumevanje Spark optimizacija")
print("="*70)
result.explain("extended")

print("\nRezultat upita:")
result.show(truncate=False)

# ============================================
# 8. FORMATI PODATAKA - CSV vs PARQUET
# ============================================
# Pravimo agregaciju po uredjajima za testiranje formata
device_agg = df.groupBy("device").agg(
    sum("revenue").alias("total_revenue"),
    avg("ctr").alias("avg_ctr"),
    count("user_id").alias("total_events")
)

# CSV format - tekstualni, citljiv ali neefikasan
print("\nCuvanje kao CSV (tekstualni format)...")
start = time.time()
device_agg.write.mode("overwrite").option("header", "true").csv("output.csv")
csv_time = time.time() - start

# Parquet format - binarni, kolonarni, kompresovan
# Prednosti: manji fajlovi, brze citanje, cuva semu
print("Cuvanje kao Parquet (kolonarni binarni format)...")
start = time.time()
device_agg.write.mode("overwrite").parquet("output_parquet")
parquet_time = time.time() - start


def get_folder_size(path):
    """Pomocna funkcija za izracunavanje velicine foldera"""
    total = 0
    for root, dirs, files in os.walk(path):
        for file in files:
            if file.startswith("part-"):
                total += os.path.getsize(os.path.join(root, file))
    return total


csv_size = get_folder_size("output.csv")
parquet_size = get_folder_size("output_parquet")

print("\n" + "="*70)
print("POREDJENJE FORMATA")
print("="*70)
print(f"CSV vreme cuvanja:     {csv_time:.3f}s")
print(f"Parquet vreme cuvanja: {parquet_time:.3f}s")
print(f"CSV velicina:          {csv_size / 1024:.2f}KB")
print(f"Parquet velicina:      {parquet_size / 1024:.2f}KB")

if csv_size > 0:
    usteda = (1 - parquet_size / csv_size) * 100
    print(f"Ustedja u prostoru:     {usteda:.1f}%")
    if usteda < 0:
        print("Napomena: Parquet je veci od CSV-a jer su podaci mali (samo 3 reda).")
        print("Na 1GB podataka, Parquet bi bio 60-80% manji od CSV-a.")
else:
    print("Nema podataka za poredjenje.")

# ============================================
# 9. ZATVARANJE SPARK SESIJE
# ============================================
print("\n" + "="*70)
print("SPARK OPTIMIZACIJE - ZAVRŠENO")
print("="*70)
print("KLJUCNI ZAKLJUCCI:")
print("1. Kesiranje ubrzava ponovne upite")
print("2. Broadcast join eliminiše shuffle za male tabele")
print("3. Particionisanje utiče na paralelizaciju")
print("4. Parquet je efikasniji od CSV-a za velike podatke")
print("5. Spark UI na http://localhost:4040 prikazuje fizički plan")

print("\nSpark UI dostupan na: http://localhost:4040")
print("Pritisni Enter da ugasiš Spark...")
input()

spark.stop()