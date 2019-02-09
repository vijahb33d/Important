/*
 * Created on 14/11/2007
 *
 */
package com.telstra.olb.tegcbm.job.migration.validation.activity;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import com.telstra.olb.tegcbm.job.core.ActivityException;
import com.telstra.olb.tegcbm.job.core.DefaultActivity;
import com.telstra.olb.tegcbm.job.core.IContext;
import com.telstra.olb.tegcbm.job.migration.model.OLBCompanyMigration;
import com.telstra.olb.tegcbm.job.migration.validation.comparators.ResultComparator;
import com.telstra.olb.tegcbm.job.migration.validation.dao.CBMValidateDAO;
import com.telstra.olb.tegcbm.job.migration.validation.dao.PDBValidateDAO;
import com.telstra.olb.tegcbm.job.migration.validation.data.MigrationValidationDataProvider;

/**
 * @author mahesh.prabhu
 *  
 */
public class MigrationValidationActivity extends DefaultActivity {

	private CBMValidateDAO cbmDao;

	private PDBValidateDAO pdbDao;

	private List validatingMethods;

	private HashMap validatingInterfaces = null;
	
	private ResultComparator resultComparator;
	private List errorBuffer= null;
	private int failedCompanies;

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.telstra.olb.tegcbm.job.core.DefaultActivity#doExecute(java.lang.Object)
	 */
	protected void doExecute(Object input) throws ActivityException {
		OLBCompanyMigration current = (OLBCompanyMigration) input;
		String companyCode = current.getCompanyCode();

		//Call validating methods here for a given company
		log.debug("Company code is : " + companyCode);
		validateCompany(companyCode);

	}

	/* (non-Javadoc)
	 * @see com.telstra.olb.tegcbm.job.core.DefaultActivity#preExecute(java.lang.Object)
	 */
	protected void preExecute(IContext activityContext) throws ActivityException {
		errorBuffer = new ArrayList();
		failedCompanies = 0;
		setValidators();
		super.preExecute(activityContext);
	}

	/**
	 * 
	 * @param companyCode
	 */
	private boolean validateCompany(String companyCode) {
		boolean valid = true;
		HashMap validatingInterfaces = getValidators();
		String method = "";
		
		if (validatingInterfaces != null) {
			ArrayList validated = new ArrayList();
			Method meth = null;
			for (Iterator methodIter = validatingMethods.iterator(); methodIter
					.hasNext();) {

				method = (String) methodIter.next();
				log.debug("Method is : " + method);
				Object[] result = new Object[2];
				int i =0;
				for (Iterator validatorIter = validatingInterfaces.keySet()
						.iterator(); validatorIter.hasNext();) {
					
					String validator = (String) validatorIter.next();
					log.debug("validator is : " + validator);
					try {
						Class validatingClass = Class.forName(validator);
						meth = validatingClass.getMethod("getCustomerDetails", new Class[] { String.class, String.class });
						result[i] = meth.invoke(validatingInterfaces.get(validator), new Object[] { method, companyCode });
						i++;
					} catch (Exception e) {
						log.error("unable to run validation method details on "
								+ validator + " for company : " + companyCode,
								e);
					}
				}
				boolean matchSuccessful = resultComparator.compareResultSets(companyCode, result);
				validated.add(new Boolean(matchSuccessful));
				if(!matchSuccessful) {
					errorBuffer.add("CIDN : " + companyCode + " :: check failed for method => " + method);
				}
			}
			Iterator validationIter = validated.iterator();
			while (validationIter.hasNext()) {
				Boolean isValid = (Boolean) validationIter.next();
				if(!isValid.booleanValue()) {
					valid = false;
					log.info("Company : " + companyCode + " does not match in both systems");
					failedCompanies += 1;
					break;
				}
			}
			if(valid) {
				log.info("Company : " + companyCode + " matches in both systems");
			}
		}
		return valid;
	}

	/**
	 * 
	 *
	 */
	private void setValidators() {

		if (validatingInterfaces == null) {
			validatingInterfaces = new HashMap();
			validatingInterfaces.put(cbmDao.getClass().getName(), cbmDao);
			validatingInterfaces.put(pdbDao.getClass().getName(), pdbDao);
		}
	}

	/**
	 * 
	 * @return
	 */
	private HashMap getValidators() {

		return validatingInterfaces;
	}

	/**
	 * 
	 * @param className
	 * @return
	 */
	private Object getValidator(String className) {
		Object returnValidator = null;
		HashMap validators = getValidators();
		while (validators.size() > 0) {
			returnValidator = validators.get(className);
		}
		return returnValidator;
	}

	/**
	 * 
	 * @param className
	 * @param aMethod
	 * @param params
	 * @param args
	 * @throws Exception
	 */
	public Object invoke(Class className, String aMethod, Class[] params,
			Object[] args) throws Exception {
		
		Object result = null;

		Method method = className.getDeclaredMethod(aMethod, params);

		Object reflectedObject = getValidator(className.getName());

		if (reflectedObject != null) {
			result = method.invoke(reflectedObject, args);
		}
		return result;
	}

	/**
	 * @return Returns the cbmDao.
	 */
	public CBMValidateDAO getCbmDao() {
		return cbmDao;
	}

	/**
	 * @param cbmDao
	 *            The cbmDao to set.
	 */
	public void setCbmDao(CBMValidateDAO cbmDao) {
		this.cbmDao = cbmDao;
	}

	/**
	 * @return Returns the pdbDao.
	 */
	public PDBValidateDAO getPdbDao() {
		return pdbDao;
	}

	/**
	 * @param pdbDao
	 *            The pdbDao to set.
	 */
	public void setPdbDao(PDBValidateDAO pdbDao) {
		this.pdbDao = pdbDao;
	}

	/**
	 * @return Returns the validatingMethods.
	 */
	public List getValidatingMethods() {
		return validatingMethods;
	}

	/**
	 * @param validatingMethods
	 *            The validatingMethods to set.
	 */
	public void setValidatingMethods(List validatingMethods) {
		this.validatingMethods = validatingMethods;
	}
	public ResultComparator getResultComparator() {
		return resultComparator;
	}
	public void setResultComparator(ResultComparator resultComparator) {
		this.resultComparator = resultComparator;
	}
	
	/* (non-Javadoc)
	 * @see com.telstra.olb.tegcbm.job.core.DefaultActivity#postExecute(java.lang.Object)
	 */
	protected void postExecute(IContext activityContext) throws ActivityException {
		int totalCompanies = ((MigrationValidationDataProvider)getDataProvider()).getTotalCompanies();
		log.info("ERRORED COMPANIES /TOTAL number of companies processed : " + failedCompanies + "/" + totalCompanies);
		
		if(errorBuffer.size() > 0) {
			log.info("ERRORS: ");
			Iterator errorIter = errorBuffer.iterator();
			int i =1;
			while (errorIter.hasNext()) {
				String error = (String) errorIter.next();
				log.error(i + ". " + error);
				i++;
			}
		}
	}
}
