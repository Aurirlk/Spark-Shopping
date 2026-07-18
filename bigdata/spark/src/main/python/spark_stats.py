#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
Spark统计任务 - 满足大数据课程要求
功能：
1. 总数统计 - 各表记录数
2. 分组聚合 - 各品类销售额
3. 排序TopN - 热销商品Top10
结果保存回Hive，并在Spark Web UI观察执行过程
"""

from pyspark.sql import SparkSession
from pyspark.sql import functions as F
from pyspark.sql.window import Window
import time

def create_spark_session():
    """创建Spark会话"""
    return SparkSession.builder \
        .appName("ShoppingStats") \
        .config("spark.sql.warehouse.dir", "/data/ecommerce/warehouse") \
        .enableHiveSupport() \
        .getOrCreate()

def stats_total_count(spark):
    """统计1：总数统计 - 各表记录数"""
    print("\n=== 统计1：总数统计 ===")

    tables = ["ods_order", "ods_order_detail", "ods_product", "ods_user", "ods_user_behavior"]
    results = []

    for table in tables:
        count = spark.sql(f"SELECT COUNT(*) as cnt FROM shopping_warehouse.{table}").collect()[0][0]
        results.append({"table_name": table, "record_count": count})
        print(f"{table}: {count} 条记录")

    # 创建DataFrame并保存
    df = spark.createDataFrame(results)
    df.write.mode("overwrite").saveAsTable("shopping_warehouse.ads_table_stats")

    print("总数统计完成，结果已保存到 ads_table_stats")
    return df

def stats_category_sales(spark):
    """统计2：分组聚合 - 各品类销售额"""
    print("\n=== 统计2：分组聚合 - 各品类销售额 ===")

    # 读取订单明细数据
    order_detail = spark.sql("""
        SELECT
            od.product_id,
            od.product_name,
            od.price,
            od.quantity,
            od.total_amount
        FROM shopping_warehouse.ods_order_detail od
    """)

    # 读取商品数据获取分类信息
    product = spark.sql("""
        SELECT id, category_id
        FROM shopping_warehouse.ods_product
    """)

    # 关联查询
    result = order_detail.join(product, order_detail.product_id == product.id, "left") \
        .groupBy("category_id") \
        .agg(
            F.count("*").alias("order_count"),
            F.sum("quantity").alias("total_quantity"),
            F.sum("total_amount").alias("total_revenue"),
            F.avg("total_amount").alias("avg_order_amount")
        ) \
        .orderBy(F.desc("total_revenue"))

    # 显示结果
    result.show()

    # 保存结果
    result.write.mode("overwrite").saveAsTable("shopping_warehouse.ads_category_sales_stats")
    print("分组聚合统计完成，结果已保存到 ads_category_sales_stats")

    return result

def stats_top_products(spark):
    """统计3：排序TopN - 热销商品Top10"""
    print("\n=== 统计3：排序TopN - 热销商品Top10 ===")

    # 读取订单明细数据
    order_detail = spark.sql("""
        SELECT
            product_id,
            product_name,
            SUM(quantity) as total_sold,
            SUM(total_amount) as total_revenue,
            COUNT(DISTINCT order_id) as order_count
        FROM shopping_warehouse.ods_order_detail
        GROUP BY product_id, product_name
    """)

    # 添加排名
    window_spec = Window.orderBy(F.desc("total_sold"))
    result = order_detail \
        .withColumn("rank_num", F.row_number().over(window_spec)) \
        .filter(F.col("rank_num") <= 10)

    # 显示结果
    result.show()

    # 保存结果
    result.write.mode("overwrite").saveAsTable("shopping_warehouse.ads_top_products_stats")
    print("TopN统计完成，结果已保存到 ads_top_products_stats")

    return result

def main():
    """主函数"""
    print("=== 开始Spark统计任务 ===")
    print("请在执行过程中访问 Spark Web UI 观察 Stage、Task 执行过程")
    print("Spark Web UI 地址: http://localhost:4040")

    # 创建Spark会话
    spark = create_spark_session()

    try:
        # 等待用户查看Web UI
        print("\n等待10秒，请打开Spark Web UI...")
        time.sleep(10)

        # 执行统计任务
        stats_total_count(spark)
        stats_category_sales(spark)
        stats_top_products(spark)

        print("\n=== Spark统计任务完成 ===")
        print("请在Spark Web UI中截图记录Stage、Task执行过程")

    except Exception as e:
        print(f"统计任务失败: {e}")
        raise
    finally:
        print("\n按Enter键停止Spark会话...")
        input()
        spark.stop()

if __name__ == "__main__":
    main()
