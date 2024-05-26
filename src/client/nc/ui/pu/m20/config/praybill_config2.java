package nc.ui.pu.m20.config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nc.ui.uif2.factory.AbstractJavaBeanDefinition;

public class praybill_config2 extends AbstractJavaBeanDefinition{
	private Map<String, Object> context = new HashMap();
public nc.ui.pu.m20.config.PushSupplierPlatform getPushsupplier(){
 if(context.get("pushsupplier")!=null)
 return (nc.ui.pu.m20.config.PushSupplierPlatform)context.get("pushsupplier");
  nc.ui.pu.m20.config.PushSupplierPlatform bean = new nc.ui.pu.m20.config.PushSupplierPlatform();
  context.put("pushsupplier",bean);
  bean.setModel((nc.ui.uif2.model.AbstractAppModel)findBeanInUIF2BeanFactory("manageAppModel"));
  bean.setEditor((nc.ui.uif2.editor.IEditor)findBeanInUIF2BeanFactory("billFormEditor"));
  bean.setCode("begin");
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.pubapp.plugin.action.InsertActionInfo getInsertActionInfo_0(){
 if(context.get("nc.ui.pubapp.plugin.action.InsertActionInfo#0")!=null)
 return (nc.ui.pubapp.plugin.action.InsertActionInfo)context.get("nc.ui.pubapp.plugin.action.InsertActionInfo#0");
  nc.ui.pubapp.plugin.action.InsertActionInfo bean = new nc.ui.pubapp.plugin.action.InsertActionInfo();
  context.put("nc.ui.pubapp.plugin.action.InsertActionInfo#0",bean);
  bean.setActionContainer((nc.ui.uif2.actions.IActionContainer)findBeanInUIF2BeanFactory("actionsOfList"));
  bean.setActionType("notedit");
  bean.setTarget((javax.swing.Action)findBeanInUIF2BeanFactory("queryAction"));
  bean.setPos("after");
  bean.setAction(getPushsupplier());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

public nc.ui.pubapp.plugin.action.InsertActionInfo getInsertActionInfo_1(){
 if(context.get("nc.ui.pubapp.plugin.action.InsertActionInfo#1")!=null)
 return (nc.ui.pubapp.plugin.action.InsertActionInfo)context.get("nc.ui.pubapp.plugin.action.InsertActionInfo#1");
  nc.ui.pubapp.plugin.action.InsertActionInfo bean = new nc.ui.pubapp.plugin.action.InsertActionInfo();
  context.put("nc.ui.pubapp.plugin.action.InsertActionInfo#1",bean);
  bean.setActionContainer((nc.ui.uif2.actions.IActionContainer)findBeanInUIF2BeanFactory("actionsOfCard"));
  bean.setActionType("notedit");
  bean.setTarget((javax.swing.Action)findBeanInUIF2BeanFactory("queryAction"));
  bean.setPos("after");
  bean.setAction(getPushsupplier());
setBeanFacotryIfBeanFacatoryAware(bean);
invokeInitializingBean(bean);
return bean;
}

}
