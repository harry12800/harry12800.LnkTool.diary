package cn.harry12800.lnk.diary.entity;

public class Aritcle {

	public String title="";
	public String createTime;
	public String updateTime="";
	public String content;
	public int sort=0;
	@Override
	public String toString() {
		return "Aritcle [title=" + title + ", createTime=" + createTime + ", updateTime=" + updateTime + ", content="
				    + ", sort=" + sort + "]";
	}
}
