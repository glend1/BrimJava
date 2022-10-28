package com.matthey.brimjava.io;

import com.matthey.brimjava.util.EventGeneric;
import com.matthey.brimjava.util.Time;

public class StructureEvent extends EventGeneric implements Runnable {
	// AL: This shouldn't create threads, its inefficient.
	// id here should be an address perhaps.
	private Long lastAlarm = null;
	private String condition = null;
	private Object value = null;
	private String operator = null;
	private Integer eventType = 2;
	private Integer bounceMin = 15;
	/*public StructureEvent(Object condition, String operator, Object val) {
		setCondition(condition);
		setOperator(operator);
		setValue(val);
	}*/
	public StructureEvent(Integer ident, String condition, String operator) {
		setId(ident);
		setCondition(condition);
		setOperator(operator);
	}
	public StructureEvent(String condition, String operator) {
		setCondition(condition);
		setOperator(operator);
	}
	public void run() {
		// TODO: TEST: 1 test condition converts to int/bool/float from float
		switch (value.getClass().getSimpleName()) {
			case "String":
				switch (operator) {
					case "==":
						if (value.equals(condition)) {
							trigger();
						}
						break;
					case "!=":
						if (!value.equals(condition)) {
							trigger();
						}
						break;
					default:
						System.out.println(condition + " does not match for " + value.getClass().getSimpleName());
						break;
				}
				break;
			case "Boolean":
				switch (operator) {
					case "==":
						if (value == condition) {
							trigger();
						}
						break;
					case "!=":
						if (value != condition) {
							trigger();
						}
						break;
					default:
						System.out.println(condition + " does not match for " + value.getClass().getSimpleName());
						break;
				}
				break;
			case "Integer":
			case "Short":
			case "Float":
				try {
					Double dVal = new Double(value.toString());
					Double dCondition = new Double(condition.toString());
					switch (operator) {
						case ">":
							if (dVal.doubleValue() > dCondition.doubleValue()) {
								trigger();
							}
							break;
						case "<":
							if (dVal.doubleValue() < dCondition.doubleValue()) {
								trigger();
							}
							break;
						case "<=":
							if (dVal.doubleValue() <= dCondition.doubleValue()) {
								trigger();
							}
							break;
						case ">=":
							if (dVal.doubleValue() >= dCondition.doubleValue()) {
								trigger();
							}
							break;
						case "==":
							if (dVal.doubleValue() == dCondition.doubleValue()) {
								trigger();
							}
							break;
						case "!=":
							if (dVal.doubleValue() != dCondition.doubleValue()) {
								trigger();
							}
							break;
						default:
							System.out.println(dVal.doubleValue() + " does not match for " + value.getClass().getSimpleName());
							break;
					}
				} catch (ClassCastException e) {
					System.out.println("EXCEPTION " + value + " does not match for " + value.getClass().getSimpleName());
				}
				break;
			default:
				System.out.println("No handling for " + value.getClass().getSimpleName());
				break;
		}
	}
	@Override
	public void trigger() {
		// TODO: AL: remove check when !123 is resolved
		if (getEvents() != null) {
			if (!getEvents().isEmpty()) {
				boolean pass = false;
				if (getLastAlarm() == null) {
					pass = true;
					System.out.println("First time run, Event not triggered before");
				} else if ((Time.getNow() - getLastAlarm()) >= (bounceMin * 60 * 1000)) {
					pass = true;
					System.out.println(((Time.getNow() - getLastAlarm()) >= (bounceMin * 60 * 1000) / 1000) + "s till last trigger");
				} else {
					setLastAlarm();
					//System.out.println("Trigger held!");
				}
				if (pass) {
					setLastAlarm();
					getEvents().trigger();
				}
			}
		}
	}
	@Override
	public Integer getEventType() {
		return eventType;
	}
	public String getCondition() {
		return condition;
	}
	public void setCondition(String condition) {
		this.condition = condition;
	}
	public String getOperator() {
		return operator;
	}
	public void setOperator(String operator) {
		this.operator = operator;
	}
	public Object getValue() {
		return value;
	}
	public void setValue(Object value) {
		this.value = value;
	}
	public Long getLastAlarm() {
		return lastAlarm;
	}
	public void setLastAlarm() {
		lastAlarm = Time.getNow();
	}

}
