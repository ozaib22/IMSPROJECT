package com.qa.ims.controller;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.qa.ims.persistence.dao.OrderDAO;
import com.qa.ims.persistence.domain.Customer;
import com.qa.ims.persistence.domain.Item;
import com.qa.ims.persistence.domain.Order;
import com.qa.ims.utils.Utils;

/**
 * Takes in order details for CRUD functionality
 *
 */
public class OrderController implements CrudController<Order> {

	public static final Logger LOGGER = LogManager.getLogger();

    private OrderDAO orderDAO;
	private Utils utils;

	public OrderController(OrderDAO orderDAO, Utils utils) {
		super();
        this.orderDAO = orderDAO;
		this.utils = utils;
	}

	/**
	 * Reads all orders to the logger
	 */
	@Override
	public List<Order> readAll() {
		List<Order> orders = orderDAO.readAll();
		for (Order order : orders) {
			LOGGER.info(order);
		}
		return orders;
	}

	/**
	 * Creates a order by taking in user input
	 */
	@Override
	public Order create() {
		LOGGER.info("Please enter the id of the customer whos related with this order");
        Long customerId = utils.getLong();
        Customer customer = orderDAO.getCustomer(customerId);
		LOGGER.info("Please enter all of ids of the items related with this order, for example, 1 2 3 4 ");
		String itemIds = utils.getString();
        Order order = null;
        if (null == customer) {
          LOGGER.info("The given customer ID is invalid, try it again");
          return order;
        }
        List<Item> items = orderDAO.getItems(itemIds);
        if (null == items || items.isEmpty()) {
          LOGGER.info("The given item IDs are invalid, try it again");
          return order;
        }
        order = orderDAO.create(new Order(customer, items));
        LOGGER.info("Order created");
		return order;
	}

	/**
	 * Updates an existing order by taking in user input
	 */
	@Override
	public Order update() {
		LOGGER.info("Please enter the id of the order you would like to update");
		Long id = utils.getLong();
        LOGGER.info("Please enter the id of the customer who related with this order");
        Long customerId = utils.getLong();
        LOGGER.info("Please enter all of ids of the items related with this order, for example, 1 2 3 4 ");
        String itemIds = utils.getString();
        Order order = orderDAO.read(id);
        order.setCustomer(orderDAO.getCustomer(customerId));
        order.setItems(orderDAO.getItems(itemIds));
		order = orderDAO.update(order);
		LOGGER.info("Customer Updated");
		return order;
	}

    /**
     * Add an item to an order
     */
    public Order addItem() {
        LOGGER.info("Please enter the id of the order you would like to update");
        Long id = utils.getLong();
        LOGGER.info("Please enter the id of the item related with this order");
        Long itemId = utils.getLong();
        Order order = orderDAO.read(id);
        Item item = orderDAO.getItem(itemId);
        if (null == item) {
          LOGGER.info("The given id of the item is invalid");
          return order;
        }
        order.getItems().add(item);
        order = orderDAO.update(order);
        LOGGER.info("The given item has been added");
        return order;
    }

    /**
     * Delete an item from an order
     */
    public Order deleteItem() {
        LOGGER.info("Please enter the id of the order you would like to update");
        Long id = utils.getLong();
        LOGGER.info("Please enter the id of the item related with this order");
        Long itemId = utils.getLong();
        Order order = orderDAO.read(id);
        List<Item> items = order.getItems();
        
        if (null == items || items.isEmpty()) {
          LOGGER.info("The given id of the item is invalid");
          return order;
        }
        boolean found = false;
        for (Item item: items) {
          if (item.getId().longValue() == itemId) {
            found = true;
            items.remove(item);
          }
        }
        if(found) {
          order = orderDAO.update(order);
          LOGGER.info("The given item has been removed");
        } else {
          LOGGER.info("The given item is invalid");
        }
        return order;
    }
    
    /**
     * Add an item to an order
     */
    public double calculate() {
        LOGGER.info("Please enter the id of the order you would like to calculate");
        Long id = utils.getLong();
        Order order = orderDAO.read(id);
        double result = 0.0;
        List<Item> items = order.getItems();
        if (null != items && !items.isEmpty()) {
          for (Item item : items) {
            result += item.getValue().doubleValue();
          }
        }
        LOGGER.info("Its cost is " + result);
        return result;
    }

	/**
	 * Deletes an existing customer by the id of the customer
	 * 
	 * @return
	 */
	@Override
	public int delete() {
		LOGGER.info("Please enter the id of the order you would like to delete");
		Long id = utils.getLong();
		return orderDAO.delete(id);
	}

}
