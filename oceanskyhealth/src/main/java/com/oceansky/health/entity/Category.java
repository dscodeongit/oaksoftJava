package com.oceansky.health.entity;

import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Field;
import org.mongodb.morphia.annotations.Id;
import org.mongodb.morphia.annotations.Index;
import org.mongodb.morphia.annotations.IndexOptions;
import org.mongodb.morphia.annotations.Indexes;
import org.mongodb.morphia.annotations.Reference;
import org.mongodb.morphia.annotations.Transient;
import org.mongodb.morphia.mapping.Mapper;
import org.mongodb.morphia.query.Query;

@Entity(value="category", noClassnameStored=true)
@Indexes({
    @Index(fields = @Field("name"), options = @IndexOptions(unique = true))
   }
)
public class Category extends MongoEntity{
	
	@Reference
	private PCategory parent;
	
	@Id
	private String name;
	
	@Transient
	private boolean isNew;

	public Category(){
	}
	
	public static Category createNew(PCategory parent, String name){
		return new Category(parent, name);
	}
	
	private Category(PCategory parent, String name){
		super();
		this.parent = parent;
		this.name = name;		
		this.isNew = true;
	}
	
	public PCategory getParent() {
		return parent;
	}

	public void setParent(PCategory parent) {
		this.parent = parent;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isNew() {
		return isNew;
	}

	public void setNew(boolean isNew) {
		this.isNew = isNew;
	}
	

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Category other = (Category) obj;
		
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Category [parent=" + parent.getName() + ", name=" + name + "]";
	}

	@Override
	public Query<Category> identityQuery(Datastore ds) {
		// TODO Auto-generated method stub
	      return ds.createQuery(Category.class).field(Mapper.ID_KEY).equal(name);

	}	
		
	/*
	HEALTH_ADAULT(PCategory.HEALTH),
	HEALTH_KID(PCategory.HEALTH),
	HEALTH_ALL(PCategory.HEALTH),
	
	COACH_BAG(PCategory.FASHION);

	PCategory parentCat;
	
	Category(PCategory pCat){
		this.parentCat = pCat;
	}
	*/
}
