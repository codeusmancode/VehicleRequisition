package view;

import java.util.HashMap;
import java.util.Map;

import javax.el.ELContext;
import javax.el.ExpressionFactory;
import javax.el.ValueExpression;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import oracle.adf.model.BindingContext;

import oracle.adf.model.binding.DCBindingContainer;
import oracle.adf.model.binding.DCIteratorBinding;
import oracle.adf.share.ADFContext;
import oracle.adf.view.rich.component.rich.data.RichTable;
import oracle.adf.view.rich.context.AdfFacesContext;
import oracle.adf.view.rich.event.DialogEvent;

import oracle.adf.view.rich.render.ClientEvent;

import oracle.apps.fnd.ext.common.AppsRequestWrapper;
import oracle.apps.fnd.ext.common.AppsSessionHelper;
import oracle.apps.fnd.ext.common.Session;

import oracle.binding.BindingContainer;
import oracle.binding.OperationBinding;

import oracle.jbo.Row;
import oracle.jbo.ViewObject;

public class VehicleRequisition {
    private RichTable reqTable;

    public VehicleRequisition() {
    }

    public BindingContainer getBindings() {
        return BindingContext.getCurrent().getCurrentBindingsEntry();
    }


    public String createNewRequisition() {
        BindingContainer bindings = getBindings();
        OperationBinding operationBinding = bindings.getOperationBinding("CreateInsert");
        Object result = operationBinding.execute();
        if (!operationBinding.getErrors().isEmpty()) {
            return null;
        }
        
        
        return null;

    }
    public static Object evaluateEL(String el) {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        ELContext elContext = facesContext.getELContext();
        ExpressionFactory expressionFactory = facesContext.getApplication().getExpressionFactory();
        ValueExpression exp = expressionFactory.createValueExpression(elContext, el, Object.class);

        return exp.getValue(elContext);
    }
    
    
    public String saveReq() {
        System.out.println("save req called");
        BindingContainer bindings = getBindings();
        OperationBinding operationBinding = bindings.getOperationBinding("Commit");
        Object result = operationBinding.execute();
        if (!operationBinding.getErrors().isEmpty()) {
            System.out.println("2");
            return null;
        }
        System.out.println("3");
        
        //INITIATE THE WORKFLOW
        //FIND THE METHOD IN ROWIMPL CLASS OF VEHICLEREQHEADERVO
        Row curr = (Row) evaluateEL("#{bindings.CustVehicleReqHeaderVO1Iterator.currentRow}");
        int reqID = Integer.parseInt(curr.getAttribute("VrReqId").toString());
        
        Map params = new HashMap<Object, Object>();
        params.put("reqID", reqID);
        operationBinding = bindings.getOperationBinding("initiateWorkflow");
        operationBinding.getParamsMap().putAll(params);
        
        result = operationBinding.execute();
        if (!operationBinding.getErrors().isEmpty()) {
            return null;
        }
        
        showMessage("Your request has been submitted to you HOD for approval.");
        
        return null;
    }

   
    
    public void showMessage(String message){
        FacesMessage Message = new FacesMessage(message);   
        Message.setSeverity(FacesMessage.SEVERITY_INFO);   
        FacesContext fc = FacesContext.getCurrentInstance();   
        fc.addMessage(null, Message);    
        
    }
    private ViewObject getViewObject(String iterator) {
        BindingContainer bindings = getBindings();
        DCBindingContainer bc = (DCBindingContainer) bindings;
        DCIteratorBinding it = bc.findIteratorBinding(iterator);
        return it.getViewObject();
    }
    public String refresh() {
        getViewObject("CustVehicleReqHeaderVO1Iterator").executeQuery();
        return null;
    }

  
    
    public String logout() {
        try{
//            ViewObject v=  getViewObject("CustVehicleReqHeaderVO1Iterator");
//            v.setNamedWhereClauseParam("USER_ID", 0);
//            v.executeQuery();
            
            
            HttpServletRequest req = (HttpServletRequest) FacesContext.getCurrentInstance()
                                                                      .getExternalContext()
                                                                      .getRequest();
            HttpServletResponse res = (HttpServletResponse) FacesContext.getCurrentInstance()
                                                                        .getExternalContext()
                                                                        .getResponse();
            AppsRequestWrapper wrappedRequest =
                new AppsRequestWrapper(req, res, ConnectionProvider.getConnection(), EbizUtil.getEBizInstance());
            Session session = wrappedRequest.getAppsSession();
            if (session != null){
                AppsSessionHelper helper = new AppsSessionHelper(wrappedRequest.getEbizInstance());
                helper.destroyAppsSession(session, req, res);
            }
            session.invalidate();
            
            //invalidate the browser session also
            FacesContext.getCurrentInstance().getExternalContext().invalidateSession();
            
            res.sendRedirect(wrappedRequest.getEbizInstance().getAppsServletAgent() + "AppsLocalLogin.jsp");
            FacesContext.getCurrentInstance().responseComplete();
        }catch(Exception ex){
            System.out.println("##########################################################");
            System.out.println(ex.getMessage());
            System.out.println("##########################################################");
        }
        return null;
    }

    public void executeAll(ClientEvent clientEvent) {
        getViewObject("CustVehicleReqHeaderVO1Iterator").executeQuery();
        System.out.println("View objects refreshed");
    }

    public String home() {
        try{
//            ViewObject v=  getViewObject("CustVehicleReqHeaderVO1Iterator");
//            v.setNamedWhereClauseParam("USER_ID", 0);
//            v.executeQuery();
            
            
            HttpServletResponse res = (HttpServletResponse) FacesContext.getCurrentInstance()
                                                                        .getExternalContext()
                                                                        .getResponse();
            res.sendRedirect("http://erpapps.usaparel.com:8000/OA_HTML/OA.jsp?OAFunc=OAHOMEPAGE"); 
            
        }catch(Exception ex){
            ex.printStackTrace();
        }
        
        return null;
    }

    public void setReqTable(RichTable reqTable) {
        //getViewObject("CustVehicleReqHeaderVO1Iterator").executeQuery();
        System.out.println("called");
        //AdfFacesContext.getCurrentInstance().addPartialTarget(reqTable);
        this.reqTable = reqTable;
    }

    public RichTable getReqTable() {
        return reqTable;
    }
}
