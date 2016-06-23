package com.sunchenbin.store.model.test;

import java.sql.Date;

import com.sunchenbin.store.annotation.Column;
import com.sunchenbin.store.annotation.Table;
import com.sunchenbin.store.constants.MySqlTypeConstant;

@Table(name = "test2")
public class Test2{

	@Column(name = "id",type = MySqlTypeConstant.INT,length = 11,isNull = false,isKey = true,isAutoIncrement = true)
	private int		id;

	@Column(name = "name",type = MySqlTypeConstant.VARCHAR,length = 100,defaultValue="dsfw")
	private String	name;

	@Column(name = "description",type = MySqlTypeConstant.TEXT,length = 1000)
	private String	description;

	@Column(name = "update_time",type = MySqlTypeConstant.DATETIME,length = 111,isNull=false)
	private Date	update_time;

	@Column(name = "number",type = MySqlTypeConstant.INT,length = 8)
	private Long	number;

	@Column(name = "lifecycle",type = MySqlTypeConstant.CHAR,length = 3)
	private String	lifecycle;

	public int getId(){
		return id;
	}

	public void setId(int id){
		this.id = id;
	}

	public String getName(){
		return name;
	}

	public void setName(String name){
		this.name = name;
	}

	public Date getUpdate_time(){
		return update_time;
	}

	public void setUpdate_time(Date update_time){
		this.update_time = update_time;
	}

	public String getDescription(){
		return description;
	}

	public void setDescription(String description){
		this.description = description;
	}

	public Long getNumber(){
		return number;
	}

	public void setNumber(Long number){
		this.number = number;
	}

	public String getLifecycle(){
		return lifecycle;
	}

	public void setLifecycle(String lifecycle){
		this.lifecycle = lifecycle;
	}

}
