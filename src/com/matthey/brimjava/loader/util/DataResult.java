package com.matthey.brimjava.loader.util;

public class DataResult {
	private Integer id = null;
	private Integer dataId = null;
	private String condition = null;
	private String operator = null;
	public DataResult(Integer id, Integer dataId, String condition, String operator) {
		setId(id);
		setDataId(dataId);
		setCondition(condition);
		setOperator(operator);
	}
	public String getOperator() {
		return operator;
	}
	public void setOperator(String operator) {
		this.operator = operator;
	}
	public String getCondition() {
		return condition;
	}
	public void setCondition(String condition) {
		this.condition = condition;
	}
	public Integer getDataId() {
		return dataId;
	}
	public void setDataId(Integer dataId) {
		this.dataId = dataId;
	}
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
}
