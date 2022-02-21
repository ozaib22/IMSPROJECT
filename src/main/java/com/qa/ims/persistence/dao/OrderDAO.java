package com.qa.ims.persistence.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.qa.ims.persistence.domain.Customer;
import com.qa.ims.persistence.domain.Item;
import com.qa.ims.persistence.domain.Order;
import com.qa.ims.utils.DBUtils;

public class OrderDAO implements Dao<Order> {

	public static final Logger LOGGER = LogManager.getLogger();

	private List<Long> parse(String line) {
      List<Long> result = new ArrayList<>();
      StringTokenizer st = new StringTokenizer(line, " ");
      while (st.hasMoreTokens()) {
        result.add(Long.parseLong(st.nextToken().trim()));
      }
      return result;
    }
    
    public List<Item> getItems(String ids) {
      List<Item> result = new ArrayList<>();
      for(Long id: parse(ids)) {
        Item item = getItem(id);
        if (null != item)
          result.add(item);
      }
      return result;
    }
    
    public Customer getCustomer(Long id) {
      try (Connection connection = DBUtils.getInstance().getConnection();
          PreparedStatement statement = connection.prepareStatement("SELECT * FROM customers WHERE id = ?");) {
          statement.setLong(1, id);
          try (ResultSet resultSet = statement.executeQuery();) {
              resultSet.next();
              String firstName = resultSet.getString("first_name");
              String surname = resultSet.getString("surname");
              return new Customer(resultSet.getLong("id"), firstName, surname);
          }
      } catch (Exception e) {
          LOGGER.debug(e);
          LOGGER.error(e.getMessage());
      }
      return null;
    }
    
    public Item getItem(Long id) {
      try (Connection connection = DBUtils.getInstance().getConnection();
          PreparedStatement statement = connection.prepareStatement("SELECT * FROM items WHERE id = ?");) {
          statement.setLong(1, id);
          try (ResultSet resultSet = statement.executeQuery();) {
              resultSet.next();
              String name = resultSet.getString("name");
              Double value = resultSet.getDouble("value");
              return new Item(resultSet.getLong("id"), name, value);
          }
      } catch (Exception e) {
          LOGGER.debug(e);
          LOGGER.error(e.getMessage());
      }
      return null;
    }
    
    private List<Item> getItems(Long orderId) {
      try (Connection connection = DBUtils.getInstance().getConnection();
          PreparedStatement statement = connection.prepareStatement("SELECT itemId FROM maporderitem where orderId = ?");) {
          statement.setLong(1, orderId);
          ResultSet resultSet = statement.executeQuery();
          List<Item> items = new ArrayList<>();
          while (resultSet.next()) {
            items.add(getItem(resultSet.getLong("itemId")));
          }
          return items;
      } catch (SQLException e) {
          LOGGER.debug(e);
          LOGGER.error(e.getMessage());
      }
      return new ArrayList<>();
    }

	@Override
    public Order modelFromResultSet(ResultSet resultSet) throws SQLException {
      Long id = resultSet.getLong("id");
      Customer customer = getCustomer(resultSet.getLong("customerId"));
      List<Item> items = getItems(id);
      return new Order(id, customer, items);
    }
	
	private Order modelFromResultSet2(ResultSet resultSet) throws SQLException {
		Long id = resultSet.getLong("id");
		Customer customer = new Customer(resultSet.getLong("cId"), resultSet.getString("fn"), resultSet.getString("sn"));
		List<Item> items = new ArrayList<>();
		Item item = new Item(resultSet.getLong("iid"), resultSet.getString("iname"), resultSet.getDouble("value"));
		items.add(item);
        return new Order(id, customer, items);
	}

	/**
	 * Reads all orders from the database
	 * 
	 * @return A list of orders
	 */
	@Override
	public List<Order> readAll() {
		try (Connection connection = DBUtils.getInstance().getConnection();
				Statement statement = connection.createStatement();
				ResultSet resultSet = statement.executeQuery("SELECT "
				    + "customers.first_name fn, customers.surname sn, customers.id cid,  "
                    + "items.name iname, items.value value, items.id iid, orders.id id "
				    + "FROM orders "
				    + "INNER JOIN customers on orders.customerId = customers.id "
				    + "INNER JOIN maporderitem on maporderitem.orderId = orders.id "
				    + "INNER JOIN items on maporderitem.itemId = items.id ");) {
			List<Order> orders = new ArrayList<>();
			while (resultSet.next()) {
			  addRecord(orders, modelFromResultSet2(resultSet));
			}
			return orders;
		} catch (SQLException e) {
			LOGGER.debug(e);
			LOGGER.error(e.getMessage());
		}
		return new ArrayList<>();
	}
	
	private void addRecord(List<Order> orders, Order order) {
	  if (orders.isEmpty()) {
	    orders.add(order);
	  } else {
	    boolean found = false;
	    for(Order existOrder: orders) {
	      if (existOrder.getId().longValue() == order.getId().longValue()) {
	        existOrder.getItems().addAll(order.getItems());
	        found = true;
	        break;
	      }
	    }
	    if (!found) {
	      orders.add(order);
	    }
	  }
	}

	public Order readLatest() {
		try (Connection connection = DBUtils.getInstance().getConnection();
				Statement statement = connection.createStatement();
				ResultSet resultSet = statement.executeQuery("SELECT * FROM orders ORDER BY id DESC LIMIT 1");) {
			resultSet.next();
			return modelFromResultSet(resultSet);
		} catch (Exception e) {
			LOGGER.debug(e);
			LOGGER.error(e.getMessage());
		}
		return null;
	}
    
    private void deleteMapping(Long orderId) {
      try (Connection connection = DBUtils.getInstance().getConnection();
          PreparedStatement statement = connection.prepareStatement("DELETE from maporderitem where orderId = ?");) {
          statement.setLong(1, orderId);
          statement.executeUpdate();
      } catch (SQLException e) {
          LOGGER.debug(e);
          LOGGER.error(e.getMessage());
      }
    }
	
	private void updateMapping(Order order) {
	  deleteMapping(order.getId());
      try (Connection connection = DBUtils.getInstance().getConnection()) {
        for (Item item: order.getItems()) {
          try (PreparedStatement statement = connection.prepareStatement("INSERT INTO maporderitem (orderId, itemId) VALUES (?, ?)");) {
              statement.setLong(1, order.getId());
              statement.setLong(2, item.getId());
              statement.executeUpdate();
          } catch (Exception e1) {
              LOGGER.debug(e1);
              LOGGER.error(e1.getMessage());
          }
        }
      } catch (Exception e) {
          LOGGER.debug(e);
          LOGGER.error(e.getMessage());
      }
	}

	/**
	 * Creates a customer in the database
	 * 
	 * @param order - takes in a order object. id will be ignored
	 */
	@Override
	public Order create(Order order) {
		try (Connection connection = DBUtils.getInstance().getConnection();
				PreparedStatement statement = connection
						.prepareStatement("INSERT INTO orders(customerId) VALUES (?)");) {
			statement.setLong(1, order.getCustomer().getId());
			statement.executeUpdate();
			Order savedOrder = readLatest();
			savedOrder.setItems(order.getItems());
			updateMapping(savedOrder);
            return savedOrder;
		} catch (Exception e) {
			LOGGER.debug(e);
			LOGGER.error(e.getMessage());
		}
		return null;
	}

	@Override
	public Order read(Long id) {
		try (Connection connection = DBUtils.getInstance().getConnection();
				PreparedStatement statement = connection.prepareStatement("SELECT * FROM orders WHERE id = ?");) {
			statement.setLong(1, id);
			try (ResultSet resultSet = statement.executeQuery();) {
				resultSet.next();
				return modelFromResultSet(resultSet);
			}
		} catch (Exception e) {
			LOGGER.debug(e);
			LOGGER.error(e.getMessage());
		}
		return null;
	}

	/**
	 * Updates a customer in the database
	 * 
	 * @param customer - takes in a customer object, the id field will be used to
	 *                 update that customer in the database
	 * @return
	 */
	@Override
	public Order update(Order order) {
		try (Connection connection = DBUtils.getInstance().getConnection();
				PreparedStatement statement = connection
						.prepareStatement("UPDATE orders SET customerId = ? WHERE id = ?");) {
			statement.setLong(1, order.getCustomer().getId());
			statement.setLong(2, order.getId());
			statement.executeUpdate();
            updateMapping(order);
			return read(order.getId());
		} catch (Exception e) {
			LOGGER.debug(e);
			LOGGER.error(e.getMessage());
		}
		return null;
	}

	/**
	 * Deletes a order in the database
	 * 
	 * @param id - id of the order
	 */
	@Override
	public int delete(long id) {
        deleteMapping(id);
		try (Connection connection = DBUtils.getInstance().getConnection();
				PreparedStatement statement = connection.prepareStatement("DELETE FROM orders WHERE id = ?");) {
			statement.setLong(1, id);
			return statement.executeUpdate();
		} catch (Exception e) {
			LOGGER.debug(e);
			LOGGER.error(e.getMessage());
		}
		return 0;
	}

}
