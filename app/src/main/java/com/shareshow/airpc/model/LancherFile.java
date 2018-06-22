package com.shareshow.airpc.model;

import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;

import java.io.Serializable;

/**
 * 保存在数据库的对象
 * @author tanwei
 *
 */
@Table(name="LancherFile")
public class LancherFile implements Serializable {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	@Column(name = "id", isId = true)
	private int id=-1;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	@Column(name = "name")
	private String name;

	@Column(name = "path")
	private String path;

	@Column(name = "size")
	private long size;

	@Column(name = "update_")
	private long update_;

	@Column(name = "permit")
	private int permit;

	@Column(name = "type")
	private int type;

	@Column(name = "fileDir")
	private int fileDir=1;//1 文件 0文件夹

	public int getFileDir() {
		return fileDir;
	}

	public void setFileDir(int fileDir) {
		this.fileDir = fileDir;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public long getSize() {
		return size;
	}

	public void setSize(long size) {
		this.size = size;
	}

	public long getUpdate() {
		return update_;
	}

	public void setUpdate(long update) {
		this.update_ = update;
	}

	public int getPermit() {
		return permit;
	}

	public void setPermit(int permit) {
		this.permit = permit;
	}

	public LancherFile() {

	}

	public LancherFile(String name, String path, long size, long update_, int type) {
		this.name=name;
		this.path=path;
		this.size=size;
		this.update_=update_;
		this.type=type;
	}

	public LancherFile(String name, String path, int type) {
		this.name=name;
		this.path=path;
		this.type=type;
	}

	@Override
	public String toString() {
		return "LancherFile{" +
				"id=" + id +
				", name='" + name + '\'' +
				", path='" + path + '\'' +
				", size=" + size +
				", update_=" + update_ +
				", permit=" + permit +
				", type=" + type +
				", fileDir=" + fileDir +
				'}';
	}
}
