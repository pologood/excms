package cn.lonsun.site.words.internal.vo;

public class CheckWordsVO {
		private Long id;
		private String words;
		private String targetWords;
		private String desc;
		private Integer type;
		public Long getId() {
			return id;
		}
		public void setId(Long id) {
			this.id = id;
		}
		public String getWords() {
			return words;
		}
		public void setWords(String words) {
			this.words = words;
		}
		public String getTargetWords() {
			return targetWords;
		}
		public void setTargetWords(String targetWords) {
			this.targetWords = targetWords;
		}
		public String getDesc() {
			return desc;
		}
		public void setDesc(String desc) {
			this.desc = desc;
		}
		public Integer getType() {
			return type;
		}
		public void setType(Integer type) {
			this.type = type;
		}
		
}
