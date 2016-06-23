package com.sunchenbin.store.command;

/**
 * 用于存放创建表的字段信息
 *
 * @author sunchenbin
 * @version 2016年6月23日 下午6:11:17 
 */
public class CreateTableParam{

	private String	fieldName;

	private String	fieldType;

	private int		fieldLength;

	private int		fieldDecimalLength;

	private boolean	fieldIsNull;

	private boolean	fieldIsKey;

	private boolean	fieldIsAutoIncrement;

	private String	fieldDefaultValue;

	private int		fileTypeLength;

	public String getFieldName(){
		return fieldName;
	}

	public void setFieldName(String fieldName){
		this.fieldName = fieldName;
	}

	public String getFieldType(){
		return fieldType;
	}

	public void setFieldType(String fieldType){
		this.fieldType = fieldType;
	}

	public int getFieldLength(){
		return fieldLength;
	}

	public void setFieldLength(int fieldLength){
		this.fieldLength = fieldLength;
	}

	public int getFieldDecimalLength(){
		return fieldDecimalLength;
	}

	public void setFieldDecimalLength(int fieldDecimalLength){
		this.fieldDecimalLength = fieldDecimalLength;
	}

	public boolean isFieldIsNull(){
		return fieldIsNull;
	}

	public void setFieldIsNull(boolean fieldIsNull){
		this.fieldIsNull = fieldIsNull;
	}

	public boolean isFieldIsKey(){
		return fieldIsKey;
	}

	public void setFieldIsKey(boolean fieldIsKey){
		this.fieldIsKey = fieldIsKey;
	}

	public boolean isFieldIsAutoIncrement(){
		return fieldIsAutoIncrement;
	}

	public void setFieldIsAutoIncrement(boolean fieldIsAutoIncrement){
		this.fieldIsAutoIncrement = fieldIsAutoIncrement;
	}

	public String getFieldDefaultValue(){
		return fieldDefaultValue;
	}

	public void setFieldDefaultValue(String fieldDefaultValue){
		this.fieldDefaultValue = fieldDefaultValue;
	}

	public int getFileTypeLength(){
		return fileTypeLength;
	}

	public void setFileTypeLength(int fileTypeLength){
		this.fileTypeLength = fileTypeLength;
	}

}
