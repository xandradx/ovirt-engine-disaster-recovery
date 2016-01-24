package models;

import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.MappedSuperclass;
import javax.persistence.PostLoad;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;

import org.apache.poi.hssf.record.formula.functions.T;
import play.db.jpa.Model;

@MappedSuperclass
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class BaseModel extends Model {
    
	@Column(name="creation_date", nullable=false)
	public Date creationDate;
	
	@Column(name="modified_date", nullable=false)
	public Date modifiedDate;
	
	public boolean active = true;
	
	@PrePersist
	private void prePersist() {
		this.creationDate = new Date();
		this.modifiedDate = new Date();
		this.onPrePersist();
	}
	
	@PreUpdate
	private void preUpdate() {
		this.modifiedDate = new Date();
		this.onPreUpdate();
	}
	
	@PostLoad
	private void postLoad() {
		this.onPostLoad();
	}
	
	protected void onPreUpdate() {
	}
	
	protected void onPrePersist() {
	}
	
	protected void onPostLoad() {
	}
	
	public void deactivate() {
		active = false;
		save();
	}

    public static<T extends BaseModel> List<T> findActive() {
        return T.find("active = ?", true).fetch();
    }
}
