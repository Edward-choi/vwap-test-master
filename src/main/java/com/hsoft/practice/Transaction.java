package com.hsoft.practice;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;


public class Transaction {
	private String productId;
	private long quantity;
	private double price;
	private static Map<String, ConcurrentLinkedQueue<Transaction>> transactions = new ConcurrentHashMap<>();
	private static Map<String, Double> fairValues = new ConcurrentHashMap<>();
	
	public Transaction(String productId, long quantity, double price) {
		super();
		this.productId = productId;
		this.quantity = quantity;
		this.price = price;
	}
	

	public String getProductId() {
		return productId;
	}

	public void setProductId(String productId) {
		this.productId = productId;
	}

	public long getQuantity() {
		return quantity;
	}

	public void setQuantity(long quantity) {
		this.quantity = quantity;
	}

	public double getPrice() {
		return price;
	}

	public void setPrice(double price) {
		this.price = price;
	}
	
	public static Map<String, ConcurrentLinkedQueue<Transaction>> getTransactions() {
		return transactions;
	}

	public static void setTransactions(Map<String, ConcurrentLinkedQueue<Transaction>> transactions) {
		Transaction.transactions = transactions;
	}

	public static Map<String, Double> getFairValues() {
		return fairValues;
	}

	public static void setFairValues(Map<String, Double> fairValues) {
		Transaction.fairValues = fairValues;
	}
	
	public static void refreshTransactions() {
		transactions = new ConcurrentHashMap<>();
		fairValues = new ConcurrentHashMap<>();
	}

	public static void addTransaction(Transaction transaction) {
        if (!transactions.containsKey(transaction.productId)) {
            ConcurrentLinkedQueue<Transaction> queue = new ConcurrentLinkedQueue<>();
            queue.add(transaction);
            transactions.put(transaction.productId, queue);
        } else {
            if (transactions.get(transaction.productId).size() >= 5) {
                transactions.get(transaction.productId).poll();
            }
            transactions.get(transaction.productId).add(transaction);
        }
	    
	}
	
	public static void updateFairValue(String productId, double fairValue) {
		fairValues.put(productId, fairValue); 
	}
	
	public static double getFairValue(String productId) {
	    return fairValues.getOrDefault(productId, 0.0);
	}
	
	public static double getVwap(String productId) {
		ConcurrentLinkedQueue<Transaction> queue;
	    queue = transactions.get(productId);
	    
	    if (queue == null || queue.isEmpty()) {
	        return 0.0;
	    }
	    
	    double totalAmount = 0.0;
	    long totalQuantity = 0;
	    for (Transaction transaction : queue) {
	        totalAmount += transaction.getPrice() * transaction.getQuantity();
	        totalQuantity += transaction.getQuantity();
	    }
	    return totalAmount / totalQuantity;
		
	}
	
}
