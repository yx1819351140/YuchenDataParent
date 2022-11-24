package com.yuchen.data.service.utils.result;

import java.util.List;

/**
 * 分页结果
 * 
 * @author fan guoping
 * @date 2019/7/17 13:12
 * @version 1.0
 */
public class PageResult<T> {

	private Long total;
	private Long hotTotal;
	private Integer page;
	private Integer size;
	private List<T> rows;


	public PageResult(Long total, List<T> rows) {
		super();
		this.total = total;
		this.rows = rows;
	}


	public PageResult(Long total, Integer page, Integer size, List<T> rows) {
		super();
		this.total = total;
		this.page = page;
		this.size = size;
		this.rows = rows;
	}

	public PageResult(Long total, Long hotTotal, Integer page, Integer size, List<T> rows) {
		super();
		this.total = total;
		this.hotTotal = hotTotal;
		this.page = page;
		this.size = size;
		this.rows = rows;
	}

	public Long getTotal() {
		return total;
	}
	public void setTotal(Long total) {
		this.total = total;
	}

	public Long getHotTotal() {
		return hotTotal;
	}
	public void setHotTotal(Long hotTotal) {
		this.hotTotal = hotTotal;
	}

	public Integer getPage() {
		return page;
	}

	public void setPage(Integer page) {
		this.page = page;
	}

	public Integer getSize() {
		return size;
	}

	public void setSize(Integer size) {
		this.size = size;
	}

	public List<T> getRows() {
		return rows;
	}
	public void setRows(List<T> rows) {
		this.rows = rows;
	}
}
