package com.scalea.entities;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;

import org.springframework.format.annotation.DateTimeFormat;

import com.scalea.utils.Utils;

import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@Table(name="processes")
public class Process {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@NotNull
	private int status;
	
	@NotNull
	@ManyToOne
    @JoinColumn(name="user_id", nullable=false)
	private User user;
	
	@NotNull
	@ManyToOne
    @JoinColumn(name="area_id", nullable=false)
	private Area area;
	
	@NotNull
	@ManyToOne
    @JoinColumn(name="product_id", nullable=false)
	private Product product;
	
	@DateTimeFormat(pattern = "yyyy-MM-dd h:i:s")
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="started_at")
	private Date startedAt;
	
	@DateTimeFormat(pattern = "yyyy-MM-dd h:i:s")
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="stopped_at")
	private Date stoppedAt;
	
	@Column(name="elapsed_time")
	private Long elapsedTime;
	
	public void calculateElapsedTime() {
		if (this.elapsedTime == null) this.elapsedTime = 0L;
		this.elapsedTime += this.getStoppedAt().getTime() - this.getStartedAt().getTime();
	}
	
	public String getElapsedTimeToString() {
		return Utils.millisToString(this.elapsedTime);
	}

	@Override
	public String toString() {
		return "Process [status=" + status + "]";
	}
}
