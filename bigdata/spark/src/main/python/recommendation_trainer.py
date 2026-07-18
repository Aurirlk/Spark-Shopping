#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
Spark MLlib 推荐算法 - 协同过滤（ALS）
用于批量训练推荐模型
"""

from pyspark.sql import SparkSession
from pyspark.ml.recommendation import ALS
from pyspark.ml.evaluation import RegressionEvaluator
from pyspark.sql.functions import col, explode
import logging

# 配置日志
logging.basicConfig(level=logging.INFO)
logger = logging.getLogger(__name__)


class RecommendationTrainer:
    """推荐模型训练器"""

    def __init__(self, spark_master="local[*]"):
        self.spark = SparkSession.builder \
            .appName("ShoppingRecommendation") \
            .master(spark_master) \
            .config("spark.sql.warehouse.dir", "/data/ecommerce/warehouse") \
            .config("spark.driver.memory", "2g") \
            .config("spark.executor.memory", "4g") \
            .getOrCreate()

        logger.info("Spark会话创建成功")

    def load_data(self, jdbc_url, db_properties):
        """加载用户行为数据"""
        logger.info("加载用户行为数据...")

        # 从MySQL加载数据
        behavior_df = self.spark.read \
            .jdbc(url=jdbc_url, table="user_behavior", properties=db_properties)

        # 过滤购买和加购行为
        ratings_df = behavior_df \
            .filter(col("behavior_type").isin([3, 4])) \
            .select(
                col("user_id").cast("integer"),
                col("product_id").cast("integer"),
                col("weight").cast("float").alias("rating")
            )

        # 统计数据量
        count = ratings_df.count()
        logger.info(f"加载数据完成，共 {count} 条记录")

        return ratings_df

    def train_model(self, ratings_df, max_iter=10, reg_param=0.01, rank=20):
        """训练ALS模型"""
        logger.info("开始训练ALS模型...")

        # 分割训练集和测试集
        (training_df, test_df) = ratings_df.randomSplit([0.8, 0.2])

        # 构建ALS模型
        als = ALS(
            maxIter=max_iter,
            regParam=reg_param,
            rank=rank,
            userCol="user_id",
            itemCol="product_id",
            ratingCol="rating",
            coldStartStrategy="drop",
            nonnegative=True
        )

        # 训练模型
        model = als.fit(training_df)

        # 评估模型
        predictions = model.transform(test_df)
        evaluator = RegressionEvaluator(
            metricName="rmse",
            labelCol="rating",
            predictionCol="prediction"
        )
        rmse = evaluator.evaluate(predictions)
        logger.info(f"模型训练完成，RMSE: {rmse:.4f}")

        return model

    def generate_recommendations(self, model, num_recommendations=20):
        """生成所有用户推荐"""
        logger.info("生成用户推荐...")

        # 为所有用户生成推荐
        user_recs = model.recommendForAllUsers(num_recommendations)
        logger.info(f"推荐生成完成，用户数: {user_recs.count()}")

        return user_recs

    def generate_similar_products(self, model, num_similar=10):
        """生成相似商品推荐"""
        logger.info("生成相似商品推荐...")

        # 为所有商品生成相似商品
        item_recs = model.recommendForAllItems(num_similar)
        logger.info(f"相似商品生成完成，商品数: {item_recs.count()}")

        return item_recs

    def save_model(self, model, path):
        """保存模型"""
        logger.info(f"保存模型到: {path}")
        model.save(path)
        logger.info("模型保存完成")

    def save_recommendations(self, user_recs, jdbc_url, db_properties):
        """保存推荐结果到数据库"""
        logger.info("保存推荐结果到数据库...")

        # 展开推荐结果
        recs_df = user_recs \
            .select(
                col("user_id"),
                explode(col("recommendations")).alias("rec")
            ) \
            .select(
                col("user_id"),
                col("rec.product_id").alias("product_id"),
                col("rec.rating").alias("score")
            )

        # 写入数据库
        recs_df.write \
            .mode("overwrite") \
            .jdbc(url=jdbc_url, table="user_recommendations", properties=db_properties)

        logger.info("推荐结果保存完成")

    def save_similar_products(self, item_recs, jdbc_url, db_properties):
        """保存相似商品到数据库"""
        logger.info("保存相似商品到数据库...")

        # 展开相似商品结果
        similar_df = item_recs \
            .select(
                col("product_id"),
                explode(col("recommendations")).alias("rec")
            ) \
            .select(
                col("product_id").alias("source_product_id"),
                col("rec.product_id").alias("similar_product_id"),
                col("rec.rating").alias("similarity_score")
            )

        # 写入数据库
        similar_df.write \
            .mode("overwrite") \
            .jdbc(url=jdbc_url, table="product_similarity", properties=db_properties)

        logger.info("相似商品保存完成")

    def stop(self):
        """停止Spark会话"""
        self.spark.stop()
        logger.info("Spark会话已停止")


def main():
    """主函数"""
    # 配置
    JDBC_URL = "jdbc:mysql://localhost:3306/shopping?useUnicode=true&characterEncoding=utf8"
    DB_PROPERTIES = {
        "user": "root",
        "password": "123123",
        "driver": "com.mysql.cj.jdbc.Driver"
    }
    MODEL_PATH = "hdfs:///models/als_recommendation"

    # 创建训练器
    trainer = RecommendationTrainer()

    try:
        # 加载数据
        ratings_df = trainer.load_data(JDBC_URL, DB_PROPERTIES)

        # 训练模型
        model = trainer.train_model(ratings_df, max_iter=10, reg_param=0.01, rank=20)

        # 生成推荐
        user_recs = trainer.generate_recommendations(model, num_recommendations=20)
        item_recs = trainer.generate_similar_products(model, num_similar=10)

        # 保存模型
        trainer.save_model(model, MODEL_PATH)

        # 保存推荐结果
        trainer.save_recommendations(user_recs, JDBC_URL, DB_PROPERTIES)
        trainer.save_similar_products(item_recs, JDBC_URL, DB_PROPERTIES)

        logger.info("推荐系统训练完成！")

    except Exception as e:
        logger.error(f"训练失败: {e}")
        raise
    finally:
        trainer.stop()


if __name__ == "__main__":
    main()
