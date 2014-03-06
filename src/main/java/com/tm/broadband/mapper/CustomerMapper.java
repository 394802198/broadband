package com.tm.broadband.mapper;

import java.util.List;

import com.tm.broadband.model.Customer;
import com.tm.broadband.model.Page;
import com.tm.broadband.model.Plan;
import com.tm.broadband.model.User;

/**
 * mapping tm_customer, customer DAO component
 * 
 * @author Cook1fan
 *
 */

/* SELECT AREA *//* // END SELECT AREA */
/* =================================================================================== */
/* INSERT AREA *//* // END INSERT AREA */
/* =================================================================================== */
/* UPDATE AREA *//* // END UPDATE AREA */
/* =================================================================================== */
/* DELETE AREA *//* // END DELETE AREA */

public interface CustomerMapper {
	
	/* SELECT AREA */
	
	int selectExistCustomerByLoginName(String login_name);
	Customer selectCustomerByIdWithCustomerOrder(int id);
	Customer selectCustomerWhenLogin(Customer customer);
	List<Customer> selectCustomersByPage(Page<Customer> page);
	int selectCustomersSum(Page<Customer> page);
	
	/* // END SELECT AREA */
	
	/* =================================================================================== */
	
	/* INSERT AREA */
	
	void insertCustomer(Customer customer);
	
	/* // END INSERT AREA */
	
	/* =================================================================================== */
	
	/* UPDATE AREA */
	
	void updateCustomerStatus(Customer customer);
	
	/* // END UPDATE AREA */
	/* =================================================================================== */
	/* DELETE AREA *//* // END DELETE AREA */
	
}
