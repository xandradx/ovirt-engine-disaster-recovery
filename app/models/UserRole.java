package models;

import play.data.validation.MaxSize;
import play.data.validation.Required;
import play.db.jpa.Model;

import javax.persistence.Column;
import javax.persistence.Entity;

@Entity
public class UserRole extends Model {
    
	public enum RoleCode {
		ADMINISTRATOR,
		TECNICO
	}
	
	@Required
	@MaxSize(45)
	@Column(length=45, nullable=false)
	public String name;
	
	@Required
	@Column(nullable=false)
	public RoleCode code;
	
	@MaxSize(100)
	@Column(length=100, nullable=true)
	public String description;
	
}
