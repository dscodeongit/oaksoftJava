package com.oceansky.health.entity;

import java.util.Collection;

import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Field;
import org.mongodb.morphia.annotations.Id;
import org.mongodb.morphia.annotations.Index;
import org.mongodb.morphia.annotations.IndexOptions;
import org.mongodb.morphia.annotations.Indexes;
import org.mongodb.morphia.annotations.Transient;
import org.mongodb.morphia.mapping.Mapper;
import org.mongodb.morphia.query.Query;

import com.google.common.collect.Lists;

@Entity(value="pcategory", noClassnameStored=true)
@Indexes({
    @Index(fields = @Field("name"), options = @IndexOptions(unique = true))
   }
)
public class PCategory extends MongoEntity{
	
	@Id
	private String name;
	
	@Transient
	private Collection<Category> cats = Lists.newArrayList();

	@Transient
	private boolean isNew;
	
	public PCategory(){
	}
	
	public static PCategory createNew(String name){
		return new PCategory(name);
	}
	
	private PCategory(String name){
		super();
		this.name = name;	
		this.isNew = true;
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
	
	public void addCategory(Category cat){
		cats.add(cat);
	}
	
	public void addAllCategories(Collection<Category> cats){
		cats.addAll(cats);
	}

	public Collection<Category> getCats() {
		return cats;
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
		PCategory other = (PCategory) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}

	@Override
	public Query<PCategory> identityQuery(Datastore ds) {
	      return ds.createQuery(PCategory.class).field(Mapper.ID_KEY).equal(name);
	}

	/*
	HEALTH,
	FOOD,
	FASHION,
	COSMETIC
	*/
}
