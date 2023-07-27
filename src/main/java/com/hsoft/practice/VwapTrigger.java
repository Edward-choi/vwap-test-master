package com.hsoft.practice;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.hsoft.api.MarketDataListener;
import com.hsoft.api.PricingDataListener;
import com.hsoft.api.VwapTriggerListener;

/**
 * Entry point for the candidate to resolve the exercise
 */
public class VwapTrigger implements PricingDataListener, MarketDataListener {

  private final VwapTriggerListener vwapTriggerListener;
  public static final Logger LOGGING = LogManager.getLogger(VwapTrigger.class);

  /**
   * This constructor is mainly available to ease unit test by not having to provide a VwapTriggerListener
   */
  protected VwapTrigger() {
    this.vwapTriggerListener = (productId, vwap, fairValue) -> {
      // ignore
    };
  }

  public VwapTrigger(VwapTriggerListener vwapTriggerListener) {
    this.vwapTriggerListener = vwapTriggerListener;
    Transaction.refreshTransactions();
  }

  @Override
  public void transactionOccurred(String productId, long quantity, double price) {
    // This method will be called when a new transaction is received
    // You can then perform your check
    // And, if matching the requirement, notify the event via 'this.vwapTriggerListener.vwapTriggered(xxx);'
	  Transaction transaction = new Transaction(productId, quantity, price);
	    Transaction.addTransaction(transaction);
	    double vwap = 0.0;
	    double fairValue = 0.0;
	    synchronized (Transaction.class) {
	        vwap = Transaction.getVwap(productId);
	        fairValue = Transaction.getFairValue(productId);
	    }
	    synchronized (vwapTriggerListener) {
		    if (vwap > fairValue) {
		    	this.vwapTriggerListener.vwapTriggered(productId, vwap, fairValue);
		    }
	    }
  }

  @Override
  public void fairValueChanged(String productId, double fairValue) {
    // This method will be called when a new fair value is received
    // You can then perform your check
    // And, if matching the requirement, notify the event via 'this.vwapTriggerListener.vwapTriggered(xxx);'
	  double vwap = 0.0;
	  synchronized (Transaction.class) {
	        Transaction.updateFairValue(productId, fairValue);
	        vwap = Transaction.getVwap(productId);
	    }
	  synchronized (vwapTriggerListener) {
		  if (vwap > fairValue) {
			  this.vwapTriggerListener.vwapTriggered(productId, vwap, fairValue);
		  }
	  }
  }
}