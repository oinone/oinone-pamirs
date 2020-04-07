package pro.shushi.pamirs.meta.dsl.definition.helper;

import org.apache.commons.lang.StringUtils;
import org.springframework.util.CollectionUtils;
import pro.shushi.pamirs.meta.dsl.constants.DSLDefineConstants;
import pro.shushi.pamirs.meta.dsl.definition.node.Exception;
import pro.shushi.pamirs.meta.dsl.definition.node.*;
import pro.shushi.pamirs.meta.dsl.model.Path;
import pro.shushi.pamirs.meta.dsl.model.Process;
import pro.shushi.pamirs.meta.dsl.model.State;
import pro.shushi.pamirs.meta.dsl.signal.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Logic DSL
 *
 * @author d@shushi.pro
 * @version 1.0.0
 * date 2019/4/26 2:11 下午
 *
 */
public class LogicTransformHelper {

	/**
	 * transform definition to process
	 *
	 * @param definition
	 * @return
	 */
	public static List<Process> transform(Logic definition) {
		List<Process> list = new ArrayList<>();
		Map<String, Code> codeMap = definition.getNodes().stream().collect(Collectors.toMap(Code::getId, v->v));
		String[] processNames = definition.getName().split(",");
		for(String processName : processNames) {
			Process p = new Process(processName, definition.getVersion());
			if (definition.getStart() != null) {
				transfer(p, definition.getStart());
			}
			if (definition.getEnds() != null) {
				for (Return end : definition.getEnds()) {
					transfer(p, end);
				}
			}
			for (Code node : definition.getNodes()) {
				transfer(p, definition.getStart().getTo(), node, codeMap);
			}
			list.add(p);
		}

		return list;
	}

	private static void transfer(Process p, Begin start) {
		State s = new State(start.getId());
		Path path = new Path();
		path.setTo(start.getTo().getId());
		path.setExp(ParseHelper.decode(start.getTo().getExp()));
		s.getNexts().add(path);
		p.setFirst(s.getName());
		p.getStates().put(s.getName(), s);
        Start exe = new Start();
        exe.setArg("model");
		exe.setModel(start.getModel());
        s.getExes().add(exe);
	}

	private static void transfer(Process p, Return ret) {
		State s = new State(ret.getId());
		End end = new End();
		end.setName(ret.getName());
		end.setExp(ParseHelper.decode(ret.getExp()));
		s.getExes().add(end);
		p.getStates().put(s.getName(), s);
	}

	private static void transfer(Process p, To start, Code node, Map<String, Code> codeMap) {
		State s = null;
		if(StringUtils.isBlank(node.getId())){
			throw new RuntimeException("节点配置错误，未配置ID，节点："+ node.toString());
		}
		if (node instanceof If) {
			if(CollectionUtils.isEmpty(node.getTos()) && null == ((If) node).getEls()){
				throw new RuntimeException("If节点配置错误，未配置分支，节点ID："+ node.getId());
			}
			if(node.getTos().size() > 1){
				throw new RuntimeException("If节点配置错误，不允许配置多个分支，节点ID："+ node.getId());
			}
			s = new State(node.getId());
			If code = (If) node;
			String exp = ParseHelper.decode(code.getExp());
			if(!CollectionUtils.isEmpty(code.getTos())){
				code.getTos().get(0).setExp(exp);
			}
			To els = code.getEls();
			els.setExp("!("+exp+")");
			if(StringUtils.isNotBlank(els.getId())){
				code.getTos().add(els);
			}
			transferTransition(start, s, code.getTos(), codeMap);
		} else if (node instanceof Foreach) {
			s = new State(node.getId());
			Foreach code = (Foreach) node;
			transferForeach(s, code);
			transferForeachTos(start, s, code, codeMap, p);
		} else if (node instanceof New) {
			s = new State(node.getId());
			New code = (New) node;
			transferNew(s, code);
			transferTransition(start, s, code.getTos(), codeMap);
		} else if (node instanceof Assign) {
			s = new State(node.getId());
			Assign code = (Assign) node;
			transferAssign(s, code);
			transferTransition(start, s, code.getTos(), codeMap);
		} else if (node instanceof Fun) {
			s = new State(node.getId());
			Fun code = (Fun) node;
			transferFun(s, code);
			transferTransition(start, s, code.getTos(), codeMap);
		} else if (node instanceof Action) {
			s = new State(node.getId());
			Action service = (Action) node;
			transferAction(s, service.getModel(), service.getName(), service.getArg(), service);
			transferTransition(start, s, service.getTos(), codeMap);
		} else if (node instanceof Query) {
			s = new State(node.getId());
			Query service = (Query) node;
			transferQuery(s, service);
			transferTransition(start, s, service.getTos(), codeMap);
		} else if (node instanceof Create) {
			s = new State(node.getId());
			Create service = (Create) node;
			transferAction(s, service.getModel(), "CREATE", service.getArg(), service);
			transferTransition(start, s, service.getTos(), codeMap);
		} else if (node instanceof Update) {
			s = new State(node.getId());
			Update service = (Update) node;
			transferAction(s, service.getModel(), "UPDATE", service.getArg(), service);
			transferTransition(start, s, service.getTos(), codeMap);
		} else if (node instanceof Delete) {
			s = new State(node.getId());
			Delete service = (Delete) node;
			transferAction(s, service.getModel(), "DELETE", service.getArg(), service);
			transferTransition(start, s, service.getTos(), codeMap);
		} else if (node instanceof Exception) {
			s = new State(node.getId());
			Exception code = (Exception) node;
			transferTransition(start, s, code.getTos(), codeMap);
		} else if (node instanceof ExtNode){
			s = new State(node.getId());
			ExtNode service= (ExtNode) node;
			transferExtNode(s, service);
			transferTransition(start, s, service.getTos(), codeMap);
		}
		transferExceptionTos(node, codeMap, p);
		if (s != null) {
			s.setEx(node.getEx());
			p.getStates().put(s.getName(), s);
		}
	}

	private static void transferAction(State s, String model, String actionName, String arg, Code service) {
		Act action = new Act();
		action.setModel(model);
		action.setName(actionName);
		action.setArg(ParseHelper.decode(arg));

		transferTx(action, service);

		s.getExes().add(action);
	}

	private static void transferNew(State s, New newNode) {
		Obj obj = new Obj();
		obj.setModel(newNode.getModel());
		if (!CollectionUtils.isEmpty(newNode.getFields())) {
			for (Arg arg : newNode.getFields()) {
				Field field = new Field();
				field.setName(arg.getName());
				field.setExp(ParseHelper.decode(arg.getExp()));
				obj.getFields().add(field);
			}
		}
		s.getExes().add(obj);
	}

	private static void transferAssign(State s, Assign assign) {
		Obj obj = new Obj();
		obj.setArg(ParseHelper.decode(assign.getArg()));
		if(!CollectionUtils.isEmpty(assign.getFields())){
			for(Arg arg : assign.getFields()){
				Field field = new Field();
				field.setName(arg.getName());
				field.setExp(ParseHelper.decode(arg.getExp()));
				obj.getFields().add(field);
			}
		}
		s.getExes().add(obj);
	}

	private static void transferFun(State s, Fun fun) {
		Func func = new Func();
		String funName = fun.getName();
		if (StringUtils.isNotBlank(funName)) {
			String[] funs = funName.split("#");
			func.setNamespace(funs.length > 0 ? funs[0] : null);
			func.setName(funs.length > 1 ? funs[1] : null);
		}
		if (!CollectionUtils.isEmpty(fun.getArgs())) {
			for (Arg arg : fun.getArgs()) {
				Field field = new Field();
				field.setName(arg.getName());
				field.setExp(ParseHelper.decode(arg.getExp()));
				func.getFields().add(field);
			}
		}

		transferTx(func, fun);

		s.getExes().add(func);
	}

	private static void transferQuery(State s, Query query) {
		Read read = new Read();
		read.setModel(query.getModel());
		read.setAggs(query.getAggs());
		read.setGroupBy(query.getGroupBy());
		read.setPage(query.getPage());
		read.setRsql(ParseHelper.decode(query.getRsql()));
		read.setSize(query.getSize());

		transferTx(read, query);

		read.setResultSchema(ParseHelper.decode(query.getResultSchema()));
		s.getExes().add(read);
	}

	private static void transferTx(Tx tx, Code code){
		tx.setModule(code.getModule());
		tx.setIsolation(code.getIsolation());
		tx.setPropagation(code.getPropagation());
		tx.setTimeout(code.getTimeout());
		tx.setReadOnly(code.getReadOnly());
		tx.setRollbackFor(code.getRollbackFor());
		tx.setNoRollbackFor(code.getNoRollbackFor());
	}

	private static void transferForeach(State s, Foreach foreach) {
		Iterator iterator = new Iterator();
		iterator.setStart(ParseHelper.decode(foreach.getStart()));
		iterator.setEnd(ParseHelper.decode(foreach.getEnd()));
		iterator.setList(ParseHelper.decode(foreach.getList()));
		s.getExes().add(iterator);
	}

	private static void transferForeachTos(To start, State s, Foreach foreach, Map<String, Code> codeMap, Process process) {
		if(null == foreach.getEach()){
			throw new RuntimeException("Foreach节点配置错误，未配置分支，节点ID："+ foreach.getId());
		}
		List<Code> endEachCodes = new ArrayList<>();
		findEndEach(endEachCodes, foreach.getEach().getId(), codeMap);
		for(Code endEach: endEachCodes){
			if(process.getStates().containsKey(endEach.getId())){
				Path path = new Path();
				path.setTo(foreach.getId());
				path.setExp(foreach.getId() + DSLDefineConstants.CURRENT_ITERATOR_INDEX + " < " + foreach.getEnd());
				process.getStates().get(endEach.getId()).getNexts().add(path);
			}else{
				To to = new To(foreach.getId());
				to.setSys(Boolean.TRUE);
				endEach.setTos( null == endEach.getTos()?new ArrayList<>():endEach.getTos() );
				endEach.getTos().add(to);
			}
		}
		if(!CollectionUtils.isEmpty(foreach.getTos())){
			if(foreach.getTos().size() > 1){
				throw new RuntimeException("Foreach节点配置错误，不允许配置多个分支，节点ID："+ foreach.getId());
			}
			foreach.getTos().get(0).setExp(foreach.getId() + DSLDefineConstants.CURRENT_ITERATOR_INDEX + " >= " + foreach.getEnd());
			transferTransition(start, s, foreach.getTos(), codeMap);
		}
		To to = new To(foreach.getEach().getId());
		to.setExp(foreach.getId() + DSLDefineConstants.CURRENT_ITERATOR_INDEX + " <= " + foreach.getEnd());
		transferTransition(s, to);
	}

	private static void transferExceptionTos(Code throwException, Map<String, Code> codeMap, Process process) {
		To ex = throwException.getEx();
		if(null == ex || StringUtils.isBlank(ex.getId())){
			return;
		}
		List<Code> endBranchCodes = new ArrayList<>();
		findEndEach(endBranchCodes, ex.getId(), codeMap);
		if(CollectionUtils.isEmpty(throwException.getTos())){
			return;
		}
		for(Code endBranch: endBranchCodes){
			if(process.getStates().containsKey(endBranch.getId())){
				Path path = new Path();
				path.setTo(throwException.getTos().get(0).getId());
				process.getStates().get(endBranch.getId()).getNexts().add(path);
			}else{
				To to = new To(throwException.getTos().get(0).getId());
				to.setSys(Boolean.TRUE);
				endBranch.setTos( null == endBranch.getTos()?new ArrayList<>():endBranch.getTos());
				endBranch.getTos().add(to);
			}
		}
	}

	private static void transferTransition(State s, To to) {
		if(StringUtils.isBlank(to.getId())){
			throw new RuntimeException("跳转配置错误，未配置跳转接待ID，节点ID：" + s.getName());
		}
		Path path = new Path();
		path.setExp(ParseHelper.decode(to.getExp()));
		path.setTo(to.getId());
		s.getNexts().add(path);
	}

	private static Integer checkTo(To code, String currentId, String toId, Map<String, Code> codeMap, boolean isCurrentFind){
		if(null == code.getId()){
			throw new RuntimeException("跳转失败，未配置跳转节点ID：" + code.toString());
		}
		if(code.getId().equals(toId)){
			if(!isCurrentFind){
				return -1;
			}else{
				return 1;
			}
		}
		if(code.getId().equals(currentId)){
			return 1;
		}
		Code next = codeMap.get(code.getId());
		if(null == next){
			throw new RuntimeException("跳转失败，未找到节点，节点ID：" + code.getId());
		}
		if(!CollectionUtils.isEmpty(next.getTos())){
			for(To to : next.getTos()){
				Integer result = checkTo(to, currentId, toId, codeMap, isCurrentFind);
				if(0 != result){
					return result;
				}
			}
		}
		return 0;
	}

	/**
	 * 获取分支最后节点
	 *
	 * @param codes 最后节点列表
	 * @param eachId 开始查找的节点ID
	 * @param codeMap 所有节点map
	 */
	private static void findEndEach(List<Code> codes, String eachId, Map<String, Code> codeMap){
		Code each = codeMap.get(eachId);
		if(null == each){
			throw new RuntimeException("跳转失败，未找到节点，节点ID：" + eachId);
		}
		if(each instanceof If){
			if (CollectionUtils.isEmpty(each.getTos()) && null == ((If) each).getEls()) {
				throw new RuntimeException("If节点配置错误，未配置分支，节点ID：" + each.getId());
			}
			if (null != ((If) each).getEls()) {
				if (StringUtils.isBlank(((If) each).getEls().getId())) {
					return;//throw new RuntimeException("If节点配置错误，未配置分支跳转节点ID，节点ID：" + each.id);
				}
				findEndEach(codes, ((If) each).getEls().getId(), codeMap);
			}
		} else{
			if(CollectionUtils.isEmpty(each.getTos())){
				codes.add(each);
				return;
			}
		}
		for(To to : each.getTos()){
			if(StringUtils.isBlank(to.getId())){
				codes.add(each);
			}
			findEndEach(codes, to.getId(), codeMap);
		}
	}

	private static void transferExtNode(State s, ExtNode extNode) {
		ExtD extD=new ExtD();
		extD.setName(extNode.getName());
		if (!CollectionUtils.isEmpty(extNode.getArgs())) {
			for (Arg arg : extNode.getArgs()) {
				Field field = new Field();
				field.setName(arg.getName());
				field.setExp(ParseHelper.decode(arg.getExp()));
				extD.getFields().add(field);
			}
		}
		s.getExes().add(extD);
	}

	private static void transferTransition(To start, State s, List<To> tos, Map<String, Code> codeMap) {
		if (tos == null)
			return;
		for (To t : tos) {
			if(StringUtils.isBlank(t.getId())){
				throw new RuntimeException("跳转配置错误，未配置跳转接待ID，节点ID：" + s.getName());
			}
			if(!Boolean.TRUE.equals(t.isSys()) && -1 == checkTo(start, s.getName(), t.getId(), codeMap, Boolean.FALSE)){
				throw new RuntimeException("跳转配置错误，不允许跳转到前置节点，当前节点ID：" + s.getName() + "，跳转节点ID：" + t.getId());
			}
			Path path = new Path();
			path.setExp(ParseHelper.decode(t.getExp()));
			path.setTo(t.getId());
			s.getNexts().add(path);
		}
	}

	private static String number(String arg) {
		String regEx="[^0-9]";
		Pattern p = Pattern.compile(regEx);
		Matcher m = p.matcher(arg);
		return m.replaceAll("").trim();
	}

	private static Integer integer(String arg) {
		return Integer.valueOf(arg);
	}

}
