package models;

import play.data.validation.MaxSize;
import play.data.validation.Required;
import play.data.validation.Unique;
import play.libs.Crypto;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import java.util.Date;

@Entity
public class User extends BaseModel {
	
	@Required
	@MaxSize(45)
	@Unique
	@Column(length=45, nullable=false, unique=true)
	public String username;
	
	@Column(length=255, nullable=true)
	public String password;
	
	@Required
	@MaxSize(45)
	@Column(length=45, nullable=false)
	public String firstName;
	
	@Required
	@MaxSize(45)
	@Column(length=45, nullable=false)
	public String lastName;

    public boolean needsPasswordReset = false;
	
	@Column(name="last_access")
	public Date lastAccess;

    @Column(name="last_activity")
    public Date lastActivity;
	
	@ManyToOne(optional=false)
	public UserRole role;

	private void encryptPassword() {
		if (this.password!=null) {
			this.password = Crypto.encryptAES(this.password);
		}
	}
	
	private void decryptPassword() {
		if (this.password!=null) {
			this.password = Crypto.decryptAES(this.password);
		}
	}

    public String getFullName() {
        String name = "";

        if (this.firstName!=null && !this.firstName.isEmpty()) {
            name = firstName;
        }

        if (this.lastName!=null && !this.lastName.isEmpty()) {

            if (!name.isEmpty()) {
                name += " ";
            }

            name += lastName;
        }

        return name;
    }
	
	@Override
	protected void onPrePersist() {
		super.onPrePersist();
		encryptPassword();
	}
	
	@Override
	protected void onPreUpdate() {
		super.onPreUpdate();
		encryptPassword();
	}
	
	@Override
	protected void onPostLoad() {
		super.onPostLoad();
		decryptPassword();
	}
}
