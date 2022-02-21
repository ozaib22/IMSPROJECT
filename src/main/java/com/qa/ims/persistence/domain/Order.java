package com.qa.ims.persistence.domain;

import java.util.List;

public class Order {

	private Long id;
	private Customer customer;
	private List<Item> items;

	public Order(Customer customer, List<Item> items) {
		this.setCustomer(customer);;
		this.setItems(items);;
	}

	public Order(Long id, Customer customer, List<Item> items) {
		this.setId(id);
        this.setCustomer(customer);;
        this.setItems(items);;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Customer getCustomer() {
      return customer;
    }
  
    public void setCustomer(Customer customer) {
      this.customer = customer;
    }
  
    public List<Item> getItems() {
      return items;
    }
  
    public void setItems(List<Item> items) {
      this.items = items;
    }
  
    @Override
	public String toString() {
      StringBuffer sbItems = new StringBuffer(); 
      if (null != items || items.size() > 0) {
        for(Item item : items) {
          sbItems.append("(" + item.toString() + ")");
        }
      }
      return "id:" + id + " customer:(" + customer.toString() + ") items: [" + sbItems.toString() + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((customer == null) ? 0 : customer.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((items == null) ? 0 : items.hashCode());
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
		Order other = (Order) obj;
		if (getCustomer() == null) {
			if (other.getCustomer() != null)
				return false;
		} else if (!getCustomer().equals(other.getCustomer()))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return hasSameItems(other.getItems());
	}
	
	private boolean hasSameItems(List<Item> otherItems) {
      if (items == null) {
        if (otherItems != null)
          return false;
      } else if (otherItems == null) {
        return false;
      } else if (items.size() != otherItems.size()) {
        return false;
      } 
      
      for (Item item : items) {
        boolean found = false;
        for(Item oItem: otherItems) {
          if (item.equals(oItem)) {
            found = true;
            break;
          }
          if (!found) return false;
        }
      }
      
	  return true;
	}

}
