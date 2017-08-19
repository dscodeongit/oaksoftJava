package com.oceansky.health.service;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.validator.routines.EmailValidator;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.oceansky.health.dao.AdministratorDao;
import com.oceansky.health.dao.CustomerDao;
import com.oceansky.health.entity.Address;
import com.oceansky.health.entity.Customer;
import com.oceansky.health.model.ActionMessage;
import com.oceansky.health.validation.MessageKey;
import com.oceansky.health.validation.PasswordValidator;
@Service("userService")
public class UserManager {
    private Logger logger = LogManager.getLogger(this.getClass());

	@Autowired
	protected CustomerDao cusDao;
	
	@Autowired
	protected AdministratorDao adminDao;
	
	public ActionMessage addCustomer(Customer cus){
		ActionMessage validMsg = validateCustomer(cus);
		if(!validMsg.isSuccessful()){
			return validMsg;
		}
		
		logger.info("Adding Customer [ID]: " + cus.getUserId());
		cusDao.save(cus);
		
		return ActionMessage.sucess();
	}
	
	public ActionMessage updateCustomer(Customer cus, Map<String, Object> fields){
		logger.info("updating Customer: " + cus.getUserId());
		cusDao.updateFields(cus, fields);
		return ActionMessage.sucess();
	}
	
	private ActionMessage validateCustomer(Customer cus){
		if(StringUtils.isBlank(cus.getFirstName())){
			return ActionMessage.error(MessageKey.ERR_FIRST_NAME_MISSING);
		}
		if(StringUtils.isBlank(cus.getLastName())){
			return ActionMessage.error(MessageKey.ERR_LAST_NAME_MISSING);
		}
		if(StringUtils.isBlank(cus.getUserId()) || cus.getUserId().trim().length() < 6){
			return ActionMessage.error(MessageKey.ERR_USERID_INVALID);
		}
		
		if(!PasswordValidator.validate(cus.getPassword())){
			return ActionMessage.error(MessageKey.ERR_PASSWORD_INVALID);
		}
		if(!EmailValidator.getInstance(true, true).isValid(cus.getEmail())){
			return ActionMessage.error(MessageKey.ERR_EMAIL_INVALID);
		}
		
		ActionMessage addrValid = validateAddress(cus.getAddress());
		if(!addrValid.isSuccessful()){
			return addrValid;
		}
				
		return ActionMessage.sucess();
	}

	private ActionMessage validateAddress(Address address) {
		if(address == null){
			return ActionMessage.error(MessageKey.ERR_ADDR_MISSING);
		}
		
		if(StringUtils.isBlank(address.getStreetAndNumber())){
			return ActionMessage.error(MessageKey.ERR_ADDR_STREET_MISSING);
		}
		
		if(StringUtils.isBlank(address.getCity())){
			return ActionMessage.error(MessageKey.ERR_ADDR_CITY_MISSING);
		}
		
		if(StringUtils.isBlank(address.getProvince())){
			return ActionMessage.error(MessageKey.ERR_ADDR_PROV_MISSING);
		}
		
		if(StringUtils.isBlank(address.getCountry())){
			return ActionMessage.error(MessageKey.ERR_ADDR_COUNTRY_MISSING);
		}
		
		if(StringUtils.isBlank(address.getPostCode())){
			return ActionMessage.error(MessageKey.ERR_ADDR_POSTCODE_MISSING);
		}
		
		return ActionMessage.sucess();
	}

	public CustomerDao getCusDao() {
		return cusDao;
	}

	public AdministratorDao getAdminDao() {
		return adminDao;
	}
}
