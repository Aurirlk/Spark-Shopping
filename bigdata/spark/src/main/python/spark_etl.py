#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
Spark ETL任务 - 电商数据分析
功能：从Hive读取数据，完成统计分析，结果保存回Hive
"""

from pyspark.sql import SparkSession
from pyspark.sql import functions as F
from pyspark.sql.window import Window

def create_spark_session():
    """创建Spark会话"""
    return SparkSession.builder \
        .appName("ShoppingETL") \
        .config("spark.sql.warehouse.dir", "/data/ecommerce/warehouse") \
        .config("spark.sql.adaptive.enabled", "true") \
        .enableHiveSupport() \
        .getOrCreate()

def read_hive_table(spark, table_name):
    """读取Hive表"""
    return spark.sql(f"SELECT * FROM shopping_warehouse.{table_name}")

def save_to_hive(df, table_name, mode="overwrite"):
    """保存DataFrame到Hive表"""
    df.write.mode(mode).saveAsTable(f"shopping_warehouse.{table_name}")

def etl_category_sales(spark):
    """ETL: 分类销售统计"""
    print("执行分类销售统计...")

    # 读取订单明细宽表
    order_detail = read_hive_table(spark, "dwd_order_detail_wide")

    # 按分类统计
    category_stats = order_detail.groupBy("category_id") \
        .agg(
            F.countDistinct("order_id").alias("total_orders"),
            F.countDistinct("user_id").alias("total_users"),
            F.sum("quantity").alias("total_quantity"),
            F.sum("total_amount").alias("total_revenue"),
            F.avg("total_amount").alias("avg_order_amount")
        ) \
        .withColumn("rank_num", F.row_number().over(
            Window.orderBy(F.desc("total_revenue"))
        ))

    # 保存结果
    save_to_hive(category_stats, "ads_category_ranking")
    print("分类销售统计完成")

    return category_stats

def etl_hot_products(spark):
    """ETL: 热销商品Top10"""
    print("执行热销商品统计...")

    # 读取商品销售日统计
    product_sales = read_hive_table(spark, "dws_product_sales_daily")

    # 按商品统计
    hot_products = product_sales.groupBy("product_id", "product_name") \
        .agg(
            F.sum("total_quantity").alias("total_sold"),
            F.sum("total_revenue").alias("total_revenue"),
            F.countDistinct("user_id").alias("buyer_count")
        ) \
        .withColumn("rank_num", F.row_number().over(
            Window.orderBy(F.desc("total_sold"))
        )) \
        .limit(10)

    # 保存结果
    save_to_hive(hot_products, "ads_hot_products")
    print("热销商品统计完成")

    return hot_products

def etl_user_value(spark):
    """ETL: 用户价值分层"""
    print("执行用户价值分层...")

    # 读取用户消费日统计
    user_consume = read_hive_table(spark, "dws_user_consume_daily")

    # 按用户统计
    user_value = user_consume.groupBy("user_id") \
        .agg(
            F.sum("order_count").alias("total_orders"),
            F.sum("total_amount").alias("total_amount"),
            F.sum("total_quantity").alias("total_quantity")
        ) \
        .withColumn("user_level",
            F.when(F.col("total_amount") >= 10000, "高价值")
             .when(F.col("total_amount") >= 5000, "中价值")
             .when(F.col("total_amount") >= 1000, "低价值")
             .otherwise("潜在用户")
        )

    # 保存结果
    save_to_hive(user_value, "ads_user_value")
    print("用户价值分层完成")

    return user_value

def etl_conversion_funnel(spark):
    """ETL: 转化漏斗分析"""
    print("执行转化漏斗分析...")

    # 读取用户行为明细
    behavior = read_hive_table(spark, "dwd_user_behavior_detail")

    # 按日期统计各行为数量
    funnel = behavior.groupBy("behavior_date") \
        .agg(
            F.count(F.when(F.col("behavior_type") == 1, 1)).alias("view_count"),
            F.count(F.when(F.col("behavior_type") == 3, 1)).alias("favorite_count"),
            F.count(F.when(F.col("behavior_type") == 4, 1)).alias("cart_count"),
            F.count(F.when(F.col("behavior_type") == 5, 1)).alias("order_count")
        ) \
        .withColumn("conversion_rate",
            F.round(F.col("order_count") * 100.0 /
                    F.nullif(F.col("view_count"), 0), 2)
        )

    # 保存结果
    save_to_hive(funnel, "ads_conversion_funnel")
    print("转化漏斗分析完成")

    return funnel

def etl_monthly_trend(spark):
    """ETL: 月度销售趋势"""
    print("执行月度销售趋势分析...")

    # 读取订单明细宽表
    order_detail = read_hive_table(spark, "dwd_order_detail_wide")

    # 按月统计
    monthly_trend = order_detail \
        .withColumn("month", F.substring("order_date", 1, 7)) \
        .groupBy("month") \
        .agg(
            F.countDistinct("order_id").alias("order_count"),
            F.countDistinct("user_id").alias("user_count"),
            F.sum("quantity").alias("total_quantity"),
            F.sum("total_amount").alias("total_revenue")
        )

    # 保存结果
    save_to_hive(monthly_trend, "ads_monthly_trend")
    print("月度销售趋势分析完成")

    return monthly_trend

def main():
    """主函数"""
    print("=== 开始Spark ETL任务 ===")

    # 创建Spark会话
    spark = create_spark_session()

    try:
        # 执行各项ETL任务
        etl_category_sales(spark)
        etl_hot_products(spark)
        etl_user_value(spark)
        etl_conversion_funnel(spark)
        etl_monthly_trend(spark)

        print("=== Spark ETL任务完成 ===")

    except Exception as e:
        print(f"ETL任务失败: {e}")
        raise
    finally:
        spark.stop()

if __name__ == "__main__":
    main()
