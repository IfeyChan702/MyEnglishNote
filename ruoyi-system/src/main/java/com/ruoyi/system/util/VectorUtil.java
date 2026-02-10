package com.ruoyi.system.util;

import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.RealVector;

import java.util.List;

/**
 * 向量计算工具类
 * 提供余弦相似度、向量归一化等功能
 * 
 * @author ruoyi
 * @date 2025-02-10
 */
public class VectorUtil {

    /**
     * 计算两个向量的余弦相似度
     * 
     * @param vector1 向量1
     * @param vector2 向量2
     * @return 余弦相似度 (范围: -1 到 1)
     */
    public static double cosineSimilarity(List<Double> vector1, List<Double> vector2) {
        if (vector1 == null || vector2 == null || vector1.isEmpty() || vector2.isEmpty()) {
            throw new IllegalArgumentException("Vectors cannot be null or empty");
        }
        
        if (vector1.size() != vector2.size()) {
            throw new IllegalArgumentException("Vectors must have the same dimension");
        }
        
        double[] array1 = vector1.stream().mapToDouble(Double::doubleValue).toArray();
        double[] array2 = vector2.stream().mapToDouble(Double::doubleValue).toArray();
        
        RealVector v1 = new ArrayRealVector(array1);
        RealVector v2 = new ArrayRealVector(array2);
        
        double dotProduct = v1.dotProduct(v2);
        double norm1 = v1.getNorm();
        double norm2 = v2.getNorm();
        
        if (norm1 == 0 || norm2 == 0) {
            return 0.0;
        }
        
        return dotProduct / (norm1 * norm2);
    }
    
    /**
     * 计算两个向量的余弦相似度（double数组版本）
     * 
     * @param vector1 向量1
     * @param vector2 向量2
     * @return 余弦相似度
     */
    public static double cosineSimilarity(double[] vector1, double[] vector2) {
        if (vector1 == null || vector2 == null || vector1.length == 0 || vector2.length == 0) {
            throw new IllegalArgumentException("Vectors cannot be null or empty");
        }
        
        if (vector1.length != vector2.length) {
            throw new IllegalArgumentException("Vectors must have the same dimension");
        }
        
        RealVector v1 = new ArrayRealVector(vector1);
        RealVector v2 = new ArrayRealVector(vector2);
        
        double dotProduct = v1.dotProduct(v2);
        double norm1 = v1.getNorm();
        double norm2 = v2.getNorm();
        
        if (norm1 == 0 || norm2 == 0) {
            return 0.0;
        }
        
        return dotProduct / (norm1 * norm2);
    }
    
    /**
     * 归一化向量（使向量的模为1）
     * 
     * @param vector 输入向量
     * @return 归一化后的向量
     */
    public static List<Double> normalizeVector(List<Double> vector) {
        if (vector == null || vector.isEmpty()) {
            throw new IllegalArgumentException("Vector cannot be null or empty");
        }
        
        double[] array = vector.stream().mapToDouble(Double::doubleValue).toArray();
        RealVector v = new ArrayRealVector(array);
        
        double norm = v.getNorm();
        if (norm == 0) {
            throw new IllegalArgumentException("Cannot normalize a zero vector");
        }
        
        RealVector normalized = v.mapDivide(norm);
        
        double[] normalizedArray = normalized.toArray();
        List<Double> result = new java.util.ArrayList<>();
        for (double value : normalizedArray) {
            result.add(value);
        }
        
        return result;
    }
    
    /**
     * 计算向量的欧几里得距离
     * 
     * @param vector1 向量1
     * @param vector2 向量2
     * @return 欧几里得距离
     */
    public static double euclideanDistance(List<Double> vector1, List<Double> vector2) {
        if (vector1 == null || vector2 == null || vector1.isEmpty() || vector2.isEmpty()) {
            throw new IllegalArgumentException("Vectors cannot be null or empty");
        }
        
        if (vector1.size() != vector2.size()) {
            throw new IllegalArgumentException("Vectors must have the same dimension");
        }
        
        double[] array1 = vector1.stream().mapToDouble(Double::doubleValue).toArray();
        double[] array2 = vector2.stream().mapToDouble(Double::doubleValue).toArray();
        
        RealVector v1 = new ArrayRealVector(array1);
        RealVector v2 = new ArrayRealVector(array2);
        
        return v1.getDistance(v2);
    }
    
    /**
     * 计算向量的点积
     * 
     * @param vector1 向量1
     * @param vector2 向量2
     * @return 点积
     */
    public static double dotProduct(List<Double> vector1, List<Double> vector2) {
        if (vector1 == null || vector2 == null || vector1.isEmpty() || vector2.isEmpty()) {
            throw new IllegalArgumentException("Vectors cannot be null or empty");
        }
        
        if (vector1.size() != vector2.size()) {
            throw new IllegalArgumentException("Vectors must have the same dimension");
        }
        
        double[] array1 = vector1.stream().mapToDouble(Double::doubleValue).toArray();
        double[] array2 = vector2.stream().mapToDouble(Double::doubleValue).toArray();
        
        RealVector v1 = new ArrayRealVector(array1);
        RealVector v2 = new ArrayRealVector(array2);
        
        return v1.dotProduct(v2);
    }
    
    /**
     * 计算向量的模（L2范数）
     * 
     * @param vector 输入向量
     * @return 向量的模
     */
    public static double norm(List<Double> vector) {
        if (vector == null || vector.isEmpty()) {
            throw new IllegalArgumentException("Vector cannot be null or empty");
        }
        
        double[] array = vector.stream().mapToDouble(Double::doubleValue).toArray();
        RealVector v = new ArrayRealVector(array);
        
        return v.getNorm();
    }
}
