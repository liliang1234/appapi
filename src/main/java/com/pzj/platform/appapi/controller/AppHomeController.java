/**
 * piaozhijia.com Inc.
 * Copyright (c) 2004-2016 All Rights Reserved.
 */
package com.pzj.platform.appapi.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.*;
import com.google.common.math.*;
import com.pzj.base.common.global.UserGlobalDict;
import com.pzj.base.service.product.IProductInfoService;
import com.pzj.base.service.product.IProductScenicService;
import com.pzj.channel.Strategy;
import com.pzj.channel.vo.resultParam.PCStrategyResult;
import com.pzj.common.QueryPageList;
import com.pzj.common.QueryPageModel;
import com.pzj.customer.entity.Customer;
import com.pzj.framework.context.Result;
import com.pzj.modules.appapi.api.SceneService;
import com.pzj.modules.appapi.entity.JsonEntity;
import com.pzj.platform.appapi.entity.HomeClassificationConfig;
import com.pzj.platform.appapi.entity.HomeSlideConfig;
import com.pzj.platform.appapi.model.HotProductModel;
import com.pzj.platform.appapi.service.CustomerService;
import com.pzj.platform.appapi.service.ProductService;
import com.pzj.platform.appapi.service.RecommendService;
import com.pzj.product.service.ISkuProductService;
import com.pzj.product.service.ISkuScenicService;
import com.pzj.product.vo.voParam.queryParam.SpuProductQueryParamVO;
import com.pzj.product.vo.voParam.resultParam.SkuScenicResult;
import com.pzj.product.vo.voParam.resultParam.SpuProductResultVO;
import com.pzj.support.service.AppApiService;
import com.pzj.support.service.AppCarouselDTO;
import com.pzj.support.service.AppCategoryImageDTO;
import com.pzj.support.service.AppHotsDTO;
import com.pzj.support.service.utils.PageResult;
import com.pzj.support.service.utils.constant.AppConstant;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.math.RoundingMode;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Mark
 * @version $Id: AppHomeController.java, v 0.1 2016年7月22日 上午10:51:14 pengliqing Exp $
 */
@Controller
@RequestMapping("home")
public class AppHomeController {
	private static final String RESPONSEBODY_KEY = "list";
	private static final String TYPE_SCENE = "scene";//景区,演艺
	private static final String TYPE_COMMONS = "common";//通用产品
	private static final Logger logger = LoggerFactory.getLogger(AppHomeController.class);
	@Autowired
	private AppApiService appApiService;
	@Autowired
	private IProductInfoService productInfoService;
	@Autowired
	private IProductScenicService iProductScenicService;
	@Autowired
	private ProductService productService;
	@Autowired
	private SceneService sceneService;
	@Autowired
	private CustomerService customerService;
	@Autowired
	private ISkuScenicService iSkuScenicService;
	@Autowired
	RecommendService recommendService;
	@Autowired
	private ISkuProductService iSkuProductService;

	/**
	 * @api {http} http://{服务器ip地址或域名}/home/getSearchKeys 查询热搜词
	 * @apiName 查询热搜词
	 * @apiGroup AppApi首页资源接口
	 * @apiVersion 1.0.0
	 * @apiDescription 提供给app前端访问的根据省市信息查询热搜词语
	 * @apiParam {String} province 省 定位成功的情况下,保证传递此参数,定位失败的情况下 province和city才可以不传
	 * @apiParam {String} [city] 市
	 * @apiParam {String} version 旧版app没传递,新版本必须传递2.0.1!!!!!!!!服务端需要使用这个值来做兼容处理!!!!!
	 * @apiSuccess {String} String 热搜词json串
	 * @apiSuccessExample {json} Success-Response:
	 * {
	 * "message": "操作成功",
	 * "accountStr": "",
	 * "responseBody": {
	 * "currentPage" :1,
	 * "totalPage":3 ,
	 * "list" : [ "laosiji", "111" ]
	 * },
	 * "responseReq": "",
	 * "code": 10000,
	 * "responseTime": ""
	 * }
	 */
	@RequestMapping(value = "/getSearchKeys")
	@ResponseBody
	public JsonEntity getSearchKeys(HttpServletRequest request, @RequestAttribute JSONObject requestObject) {
		Long start = System.currentTimeMillis();
		List<String> keyList;
		String province = requestObject.getString("province");
		String city = requestObject.getString("city");
		Set<String> set;//用于去重的set
		try {
			Long apiCosts = System.currentTimeMillis();
			if (StringUtils.isNoneBlank(province, city)) {//定位成功:province,city都传递了
				keyList = appApiService.getSearchKeys(true, province, city);
				if (CollectionUtils.isEmpty(keyList)) {//查询该省下"默认城市"的配置
					keyList = appApiService.getSearchKeys(true, province, AppConstant.DEFAULT_CITY);
					if (CollectionUtils.isEmpty(keyList)) {//向上查询"全国"
						keyList = appApiService.getSearchKeys(true, AppConstant.NATIONAL_AREA_NAME, AppConstant.NATIONAL_AREA_NAME);
					}
				}
			} else if (StringUtils.isNotBlank(province) && StringUtils.isBlank(city)) {//定位成功:只传递了province ,city未传递
				keyList = appApiService.getSearchKeys(true, province, AppConstant.DEFAULT_CITY);
				if (CollectionUtils.isEmpty(keyList)) {//向上查询"全国"
					keyList = appApiService.getSearchKeys(true, AppConstant.NATIONAL_AREA_NAME, AppConstant.NATIONAL_AREA_NAME);
				}
			} else {//定位失败:province,city都未传递 直接查询"全国" - "全国" 的数据
				keyList = appApiService.getSearchKeys(true, AppConstant.NATIONAL_AREA_NAME, AppConstant.NATIONAL_AREA_NAME);
			}
			logger.debug("support-service耗时:{}毫秒,返回的热搜词=={}", System.currentTimeMillis() - apiCosts, JSON.toJSONString(keyList));
		} catch (Exception e) {
			logger.error("热搜词接口发生异常=={}", e);
			return JsonEntity.makeExceptionJsonEntity("00001", "热搜词语查询失败");
		}//keyList去重
		if (CollectionUtils.isNotEmpty(keyList)) {
			set = Sets.newHashSet(keyList);
			keyList = Lists.newArrayList(set);
		}
		logger.debug("热搜词语接口耗时:{}毫秒,最终返回的热搜词=={}", System.currentTimeMillis() - start, JSON.toJSONString(keyList));
		return JsonEntity.makeSuccessJsonEntity("searchKeys", generateResponseBodyMap(keyList));
	}

	/**
	 * @throws Exception
	 * @api {http} http://{服务器ip地址或域名}/home/getHots 查询热门推荐
	 * @apiName 查询热门推荐
	 * @apiGroup AppApi首页资源接口
	 * @apiVersion 1.0.0
	 * @apiDescription 提供给app前端访问的根据省市查询热门推荐
	 * @apiParam {String}  salesPort 销售渠道
	 * @apiParam {String} province 省 定位成功的情况下,保证传递此参数,定位失败的情况下 province和city才可以不传
	 * @apiParam {String} version 旧版app没传递,新版本必须传递2.0.1!!!!!!!!服务端需要使用这个值来做兼容处理!!!!!
	 * @apiParam {String} [city] 市 如不传递则查询该省下的 所有资源 (微店在市级下没有找到数据将查询所属省的数据,如果仍没有则返回空数据)
	 * @apiParam {int} [currentPage] 当前页（从1开始,不传递就默认1）
	 * @apiParam {int} [pageSize] 每页条数（大于0,不传递就默认10）
	 * @apiSuccess {String} String 热门推荐信息json串
	 * @apiSuccessExample {json} Success-Response:
	 * {
	 * "message": "操作成功",
	 * "accountStr": "",
	 * "responseBody": {
	 * "currentPage" :1,
	 * "totalPage":3 ,
	 * "list":[
	 * {"image":"c20aa7605cc83a58b37e38b2fa258828",
	 * "title":"产品组名",
	 * "remarks":"北京景区",
	 * "productGroupId":2216619741564301,
	 * "proCategory":"1",
	 * "minAdvicePrice":"50.0"
	 * }
	 * ]
	 * },
	 * "responseReq": "",
	 * "code": 10000,
	 * "responseTime": ""
	 * }
	 */
	@RequestMapping(value = "/getHots")
	@ResponseBody
	public JsonEntity newGetHots(@RequestAttribute JSONObject requestObject, @RequestAttribute Customer customer) {
		Long start = System.currentTimeMillis();
		JsonEntity jsonEntity = new JsonEntity();
		logger.debug("requestObject=={}", JSON.toJSONString(requestObject));
		String province = requestObject.getString("province");
		if(StringUtils.equalsIgnoreCase("北京市",province)){
			province = "北京";
		}
		if(StringUtils.equalsIgnoreCase("上海市",province)){
			province = "上海";
		}
		if(StringUtils.equalsIgnoreCase("重庆市",province)){
			province = "重庆";
		}
		if(StringUtils.equalsIgnoreCase("天津市",province)){
			province = "天津";
		}
		String city = requestObject.getString("city");
		String salesPort = StringUtils.isBlank(requestObject.getString("salesPort")) ? UserGlobalDict.StrategyGlobalDict.windowTenantApp() + "" : requestObject.getString("salesPort");
		Integer currentPage = requestObject.getInteger("currentPage");
		Integer pageSize = requestObject.getInteger("pageSize");
		PageResult<AppHotsDTO> pageResult;
		/** 构造接口需要的查询参数 **/
		SpuProductQueryParamVO spuProductQueryParamVO = new SpuProductQueryParamVO();
		spuProductQueryParamVO.setSalesType(salesPort);
		spuProductQueryParamVO.setDistributorId(customer.getId());
		QueryPageModel queryPageModel = new QueryPageModel();
		queryPageModel.setPageNo(1);
		queryPageModel.setPageSize(9999);
		Result<QueryPageList<SkuScenicResult>> scenicResult;//景区接口的数据
		QueryPageList<SpuProductResultVO> commonResult;//通用产品接口的数据
		Map<Long, SkuScenicResult> scenicMap = Maps.newHashMap();
		Map<Long, SpuProductResultVO> commonMap = Maps.newHashMap();
		List<AppHotsDTO> allHotsVOList;//support-service返回的列表
		Map<Long, AppHotsDTO> map;//用来对热门推荐数据去重的map
		try {/** 向上查询数据直到"全国" **/
			Long supportServiceCosts = System.currentTimeMillis();/** 查询指定城市的数据 **/
			pageResult = appApiService.findOpenApiHotsByArea(province, city, null, null, true);//support-service不再分页,查到全部数据后在api里分页
			if (CollectionUtils.isEmpty(pageResult.getList())) {/** 查询省份下每个城市全部的数据然后加起来 **/
				pageResult = appApiService.findOpenApiHotsByArea(province, null, null, null, true);
				if (CollectionUtils.isEmpty(pageResult.getList())) {/** 查询province="全国" city="全国"这个特殊区域配置的资源 ,平台保证在插入数据时,province="全国",city="全国" **/
					pageResult = appApiService.findOpenApiHotsByArea(AppConstant.NATIONAL_AREA_NAME, AppConstant.NATIONAL_AREA_NAME, null, null, true);
				}
			}
			logger.debug("supportService返回的pageResult=={},总共耗时:{}毫秒", JSON.toJSONString(pageResult), System.currentTimeMillis() - supportServiceCosts);
			if (pageResult == null || CollectionUtils.isEmpty(pageResult.getList())) {
				logger.debug("supportService返回为空数据,直接返回.");
				return JsonEntity.makeSuccessJsonEntity("hots", pageResult);
			}
			allHotsVOList = pageResult.getList();
			/** allHotsVOList所有元素存入map中在下面的迭代中进行检查处理去重 **/
			map = Maps.newHashMap();
			for (AppHotsDTO dto : allHotsVOList) {
				map.put(dto.getProductGroupId(), dto);
			}
			/** 查询新 景区 接口获取全部该用户的景区数据 **/
			Long scenicCosts = System.currentTimeMillis();
			logger.debug("传递给iSkuProductService接口的参数spuProductQueryParamVO=={},queryPageModel=={}", JSON.toJSONString(spuProductQueryParamVO), JSON.toJSONString(queryPageModel));
			scenicResult = iSkuScenicService.findSkuScenicForApp(spuProductQueryParamVO, queryPageModel);
			logger.debug("景区演艺接口总共耗时:{}毫秒", System.currentTimeMillis() - scenicCosts);
			if (null != scenicResult && null != scenicResult.getData()) {
				scenicMap = convertToMap(scenicResult.getData().getResultList());
			}
//			logger.debug("转换成scenicMap后=={}", JSON.toJSONString(scenicMap));结果太大 不能打印日志
			/** 查询 通用产品 接口获取全部该用户的景区数据 **/
			Long commonCosts = System.currentTimeMillis();
			commonResult = iSkuProductService.findSkuProductForApp(spuProductQueryParamVO, queryPageModel);
			logger.debug("通用产品接口总共耗时:{},返回结果是否为空:{}", System.currentTimeMillis() - commonCosts, commonResult == null);
			if (null != commonResult) {
				commonMap = convertToMap(commonResult.getResultList());
			}
//			logger.debug("转换成commonMap后=={}", JSON.toJSONString(commonMap));结果太大 不能打印日志
		} catch (Exception e) {
			logger.error("调用服务接口出错=={}", e);
			jsonEntity.setMessage("查询失败,请重试");
			return jsonEntity.makeExceptionJsonEntity("10001", "调用服务接口出错.");
		}
		Iterator<AppHotsDTO> $i = allHotsVOList.iterator();
		AtomicInteger removed= new AtomicInteger(0);
		while ($i.hasNext()) {/** 从map中获取元素不为空则移除,为空则忽略本次迭代 **/
			AppHotsDTO appHotsDTO = $i.next();
			Long id = appHotsDTO.getProductGroupId();
			if (null != map.get(id)) {/** 从元素唯一的map中获取指定id的热门推荐对象,如果获取到了,则删除后往下执行业务;若没获取到,说明当前迭代的这个元素是重复的,直接忽略本次迭代 **/
				map.put(id, null);
			} else {
				$i.remove();
				removed.addAndGet(1);
				continue;
			}
			/** 景区/演艺的分支 **/
			if (1 == appHotsDTO.getProCategory() || 10 == appHotsDTO.getProCategory() || 2 == appHotsDTO.getProCategory()) {
				if (!scenicMap.containsKey(id)) {//求交集
					$i.remove();
					removed.addAndGet(1);
					continue;
				}
				if (2 == appHotsDTO.getProCategory()) {//将数据库中存放的type=2的设置为10前端要用这个值做判断
					appHotsDTO.setProCategory(10);
				}
				if (StringUtils.equalsIgnoreCase(UserGlobalDict.StrategyGlobalDict.windowMicroshop() + "", salesPort)) { /** 微店查询最低建议零售价APP不管 **/
					logger.debug("景区演艺产品的最低建议零售价=={}", scenicMap.get(id).getMinPrice());
					appHotsDTO.setMinAdvicePrice(scenicMap.get(id).getMinPrice());
				}
				appHotsDTO.setRemarks(scenicMap.get(id).getSkuScenic().getInfo());
				appHotsDTO.setTitle(scenicMap.get(id).getSkuScenic().getName());
				appHotsDTO.setImage(scenicMap.get(id).getSkuScenic().getImgUrl());
			} else { /** 通用产品的分支 **/
				{//其余的按通用产品处理
					//查找是否存在于登录用户的渠道中,如果不存在从allHotsVOList中移除这个元素
					logger.debug("spuId====>{}", id);
					if (!commonMap.containsKey(id)) {//求交集
						logger.debug("spuId=>{}不在commonMap中", id);
						$i.remove();
						removed.addAndGet(1);
						continue;
					}
					if (StringUtils.equalsIgnoreCase("7", salesPort)) {//微店设置最低建议零售价APP不管
						Double minAdvicePrice = findLowestAdvicePrice(id, commonResult.getResultList());
						logger.debug("计算得到的通用产品最低建议零售价=={}", minAdvicePrice);
						appHotsDTO.setMinAdvicePrice(minAdvicePrice);
					}
					SpuProductResultVO sp = commonMap.get(id);
					appHotsDTO.setTitle(sp.getSpuProduct().getName());//设置展示名称
					appHotsDTO.setRemarks(sp.getSpuProduct().getOneWordFeature());//设置一句话介绍
					logger.debug("通用产品图片getReleaseThurl=={} ||||| getPhotoinfoId=={}", sp.getSpuProduct().getReleaseThurl(), sp.getSpuProduct().getPhotoinfoId());
					appHotsDTO.setImage(StringUtils.isBlank(sp.getSpuProduct().getReleaseThurl()) ? sp.getSpuProduct().getPhotoinfoId() : sp.getSpuProduct().getReleaseThurl());//图片容错
				}
			}
		}
		logger.debug("求交集共删除了:{}个元素",removed);
		/** 求完交集后进行分页 **/
		if (null == currentPage || 0 >= currentPage) {
			currentPage = 1;
		}
		if (null == pageSize || 0 >= pageSize) {
			pageSize = 10;
		}//如果allHotsVOList内的元素全部被移除
		pageResult.setPageSize(pageSize);
		if (CollectionUtils.isNotEmpty(allHotsVOList)) {
			pageResult.setTotalCount(allHotsVOList.size());
			logger.debug("list.size=={},pageSize=={},totalPage=={},pageResult.getTotalPage=={}", allHotsVOList.size(), pageSize, IntMath.divide(allHotsVOList.size(), pageSize, RoundingMode.HALF_UP), pageResult.getTotalPage());
			pageResult.setTotalPage(IntMath.divide(allHotsVOList.size(), pageSize, RoundingMode.HALF_UP));
			List<List<AppHotsDTO>> paged = Lists.partition(allHotsVOList, pageSize);//经过分页的不可变list
			if (currentPage > paged.size()) {//最后1页的数据
				allHotsVOList = Iterables.getLast(paged, Lists.<AppHotsDTO>newArrayList());
			} else {//返回需要的页数
				allHotsVOList = paged.get(currentPage - 1);
			}
		}
		pageResult.setList(allHotsVOList);
		pageResult.setCurrentPage(currentPage);
		logger.debug("热门推荐方法总共用时:{}毫秒", System.currentTimeMillis() - start);
		logger.debug("热门推荐方法最终返回结果=={}", JSON.toJSONString(pageResult));
		return JsonEntity.makeSuccessJsonEntity("hots", pageResult);
	}

	/**
	 * @api {http} http://{服务器ip地址或域名}/home/getIcons 查询分类图标
	 * @apiName 查询分类图标
	 * @apiGroup AppApi首页资源接口
	 * @apiVersion 1.0.0
	 * @apiParam {String} version 旧版app没传递,新版本必须传递2.0.1!!!!!!!!服务端需要使用这个值来做兼容处理!!!!!
	 * @apiDescription 提供给app前端访问的根据省市查询分类图标
	 * @apiParam {String} [province] 省 定位成功的情况下,保证传递此参数,定位失败的情况下 province和city才可以不传
	 * @apiParam {String} [city] 市 如不传递则查询该省下的所有资源
	 * @apiSuccess {String} String 分类图标信息json串
	 * @apiSuccessExample {json} Success-Response:
	 * {
	 * "message": "操作成功",
	 * "accountStr": "",
	 * "responseBody": {
	 * "currentPage" :1,
	 * "totalPage":3 ,
	 * "list":[
	 * {
	 * "appCategoryId": 8000,
	 * "image": "asdasdasd" ,
	 * "text" :"景区"
	 * },
	 * {
	 * "appCategoryId": 10,
	 * "image": "781e8dfb1d2109701933542ef971a319" ,
	 * "text" :"演艺"
	 * },
	 * {
	 * "appCategoryId": 7000,
	 * "image": "dccfa1094188a977d5a7c1e3d5d8f7c3",
	 * "text" :"一日游"
	 * }
	 * ]},
	 * "responseReq": "",
	 * "code": 10000,
	 * "responseTime": ""
	 * }
	 */
	@RequestMapping(value = "/getIcons")
	@ResponseBody
	public JsonEntity getCategoryImageByArea(@RequestAttribute JSONObject requestObject) {
		Long start = System.currentTimeMillis();
		String province = requestObject.getString("province");
		String city = requestObject.getString("city");
		List<AppCategoryImageDTO> categoryImageList;
		try {
			Long apiCosts = System.currentTimeMillis();
			if (StringUtils.isNoneBlank(province, city)) {//定位成功:province,city都传递了
				categoryImageList = appApiService.findCategoryImageByArea(province, city, true);
				if (CollectionUtils.isEmpty(categoryImageList)) {//查询该省下"默认城市"的配置
					categoryImageList = appApiService.findCategoryImageByArea(province, AppConstant.DEFAULT_CITY, true);
					if (CollectionUtils.isEmpty(categoryImageList)) {//向上查询"全国"
						categoryImageList = appApiService.findCategoryImageByArea(AppConstant.NATIONAL_AREA_NAME, AppConstant.NATIONAL_AREA_NAME, true);
					}
				}
			} else if (StringUtils.isNotBlank(province) && StringUtils.isBlank(city)) {//定位成功:只传递了province ,city未传递
				categoryImageList = appApiService.findCategoryImageByArea(province, AppConstant.DEFAULT_CITY, true);
				if (CollectionUtils.isEmpty(categoryImageList)) {//向上查询"全国"
					categoryImageList = appApiService.findCategoryImageByArea(AppConstant.NATIONAL_AREA_NAME, AppConstant.NATIONAL_AREA_NAME, true);
				}
			} else {//定位失败:province,city都未传递 直接查询"全国" - "全国" 的数据
				categoryImageList = appApiService.findCategoryImageByArea(AppConstant.NATIONAL_AREA_NAME, AppConstant.NATIONAL_AREA_NAME, true);
			}
			logger.debug("support-service查询分类图标耗时:{}毫秒,返回的分类图标=={}", System.currentTimeMillis() - apiCosts, JSON.toJSONString(categoryImageList));
		} catch (Exception e) {
			logger.error("support-service出错=={}", e);
			return JsonEntity.makeExceptionJsonEntity("00001", "查询分类图标失败");
		}/**分类图标平台保证插入数据时不存在重复,api中不做去重处理**/
		Map<String, ? extends List<AppCategoryImageDTO>> responseBody = generateResponseBodyMap(categoryImageList);
		logger.debug("分类图标接口总计耗时:{}毫秒,返回的结果=={}", System.currentTimeMillis() - start, JSON.toJSONString(responseBody));
		return JsonEntity.makeSuccessJsonEntity("icons", responseBody);
	}


	/**
	 * @api {http} http://{服务器ip地址或域名}/home/getCarousels 查询轮播图
	 * @apiName 查询轮播图
	 * @apiGroup AppApi首页资源接口
	 * @apiVersion 1.0.0
	 * @apiDescription 提供给app前端访问的根据省市查询轮播图
	 * @apiParam {String} province 省 定位成功的情况下,保证传递此参数,定位失败的情况下 province和city才可以不传
	 * @apiParam {String} [city] 市 如不传递则查询该省下的所有资源
	 * @apiParam {String} version 旧版app没传递,新版本必须传递2.0.1!!!!!!!!服务端需要使用这个值来做兼容处理!!!!!
	 * @apiSuccess {String} String 轮播图信息json串
	 * @apiSuccessExample {json} Success-Response:
	 * {
	 * "message": "操作成功",
	 * "accountStr": "",
	 * "responseBody": {
	 * "currentPage" :1,
	 * "totalPage":3 ,
	 * "list":[
	 * {
	 * "text":"测试1",
	 * "image":"bed42d3977019912fa93954e02f45e61",
	 * "appType":1,(跳转类型,1-http 2-景区ID 3-通用产品组ID)
	 * "url":"http://www.baidu.com", - 按新需求调整后,此属性可能为空
	 * "title":"标题XXXX", - 轮播图跳转后的页面标题
	 * "targetId":"12345678987654321", (需要跳转的景区ID/通用产品组ID)
	 * "proCategory":"8000" (分类ID,appType=2或3时此属性才会赋值,否则是"")
	 * }
	 * ]
	 * },
	 * "responseReq": "",
	 * "code": 10000,
	 * "responseTime": ""
	 * }
	 */
	@RequestMapping(value = "/getCarousels")
	@ResponseBody
	public JsonEntity getCarousels(String version, @RequestAttribute JSONObject requestObject) {
		Long start = System.currentTimeMillis();
		String province = requestObject.getString("province");
		String city = requestObject.getString("city");
		List<AppCarouselDTO> carouselList;
		boolean isRedis = true;
		Long apiCosts = System.currentTimeMillis();
		try {
			if (StringUtils.isNoneBlank(province, city)) {//定位成功:province,city都传递了
				carouselList = appApiService.findOpenApiCarouselByArea(province, city, isRedis);
				if (CollectionUtils.isEmpty(carouselList)) {//查询该省下"默认城市"的配置
					carouselList = appApiService.findOpenApiCarouselByArea(province, AppConstant.DEFAULT_CITY, isRedis);
					if (CollectionUtils.isEmpty(carouselList)) {//向上查询"全国"
						carouselList = appApiService.findOpenApiCarouselByArea(AppConstant.NATIONAL_AREA_NAME, AppConstant.NATIONAL_AREA_NAME, isRedis);
					}
				}
			} else if (StringUtils.isNotBlank(province) && StringUtils.isEmpty(city)) {//定位成功:只传递了province ,city未传递
				carouselList = appApiService.findOpenApiCarouselByArea(province, AppConstant.DEFAULT_CITY, isRedis);
				if (CollectionUtils.isEmpty(carouselList)) {//向上查询"全国"
					carouselList = appApiService.findOpenApiCarouselByArea(AppConstant.NATIONAL_AREA_NAME, AppConstant.NATIONAL_AREA_NAME, isRedis);
				}
			} else {//定位失败:province,city都未传递 直接查询"全国" - "全国" 的数据
				carouselList = appApiService.findOpenApiCarouselByArea(AppConstant.NATIONAL_AREA_NAME, AppConstant.NATIONAL_AREA_NAME, isRedis);
			}
			logger.debug("support-service查询轮播图耗时:{}毫秒,返回的轮播图=={}", System.currentTimeMillis() - apiCosts, JSON.toJSONString(carouselList));
		} catch (Exception e) {
			logger.error("support-service出错=={}", e);
			return JsonEntity.makeExceptionJsonEntity("00001", "查询轮播图失败");
		}/**轮播图暂时不做去重处理,新版app根据version过滤掉url]为空的数据 **/
		logger.debug("version=={}", version);
		if (!StringUtils.equalsIgnoreCase("2.0.1", version)) {
			if (CollectionUtils.isNotEmpty(carouselList)) {
				Iterator<AppCarouselDTO> $i = carouselList.iterator();
				while ($i.hasNext()) {
					AppCarouselDTO target = $i.next();
					if (StringUtils.isBlank(target.getUrl())) {
						$i.remove();
					}
				}
			}
		}//当某个区域只配置了一个轮播图资源,且是老版本的app时,如果这个轮播图的url为空会被过滤掉,则再次查询
		if(CollectionUtils.isEmpty(carouselList)){
			carouselList = appApiService.findOpenApiCarouselByArea(province, city, isRedis);
			if(CollectionUtils.isEmpty(carouselList)){
				carouselList = appApiService.findOpenApiCarouselByArea(province, AppConstant.DEFAULT_CITY, isRedis);
				if(CollectionUtils.isEmpty(carouselList)){
					carouselList = appApiService.findOpenApiCarouselByArea(AppConstant.NATIONAL_AREA_NAME, AppConstant.NATIONAL_AREA_NAME, isRedis);
				}
			}
		}
		logger.debug("轮播图接口总计耗时:{}毫秒,返回的结果为=={}", System.currentTimeMillis() - start, JSON.toJSONString(carouselList));
		return JsonEntity.makeSuccessJsonEntity("carousels", generateResponseBodyMap(carouselList));
	}

	/**
	 * 接口返回的list转map,id做key,无序,仅用于求交集
	 *
	 * @param list
	 * @param <T>
	 * @return 一个不可变的map
	 */
	private <T> ImmutableMap<Long, T> convertToMap(List<T> list) {
		Map<Long, T> map = Maps.newHashMap();
		if (CollectionUtils.isNotEmpty(list)) {
			list = ImmutableList.copyOf(list);
			for (T t : list) {
				if (null == t) {
					logger.debug("list中的元素为空忽略这个元素");
					continue;
				}/** 景区,演艺产品 **/
				if (t instanceof SkuScenicResult) {
					if (((SkuScenicResult) t).getSkuScenic() != null) {
						map.put(((SkuScenicResult) t).getSkuScenic().getId(), t);
					} else {
						logger.error("SkuScenicResult的list中存在一个空元素,忽略");
						continue;
					}
				}/** 通用产品 **/
				if (t instanceof SpuProductResultVO) {//通用产品
					if (((SpuProductResultVO) t).getSpuProduct() != null) {
						map.put(((SpuProductResultVO) t).getSpuProduct().getId(), t);
					} else {
						logger.error("SpuProductResultVO的list中存在一个空元素忽略");
						continue;
					}
				}
			}
		}
		return ImmutableMap.copyOf(map);
	}

	/**
	 * @param spuId
	 * @param list
	 * @return 指定spuId下最低建议零售价
	 */
	private static Double findLowestAdvicePrice(Long spuId, List<SpuProductResultVO> list) {
		Long start = System.currentTimeMillis();
		if (CollectionUtils.isEmpty(list) || null == spuId) {
			logger.debug("查询最低建议零售价,list为空,spuId==>{}", spuId);
			return null;
		}
		Set<Double> priceSet = new TreeSet<>();
		try {
			for (SpuProductResultVO vo : list) {
				if (spuId.equals(vo.getSpuProduct().getId())) {
					logger.debug("spuId===>{},SpuProductResultVO==>{}", spuId, JSON.toJSONString(vo.getSpuProduct()));
					Map<Long, List<PCStrategyResult>> strageMap = vo.getStrategyList();
					for (Long skuId : strageMap.keySet()) {
						for (PCStrategyResult pCStrategy : strageMap.get(skuId)) {
							for (Strategy strategy : pCStrategy.getStrategyList()) {
								if (pCStrategy != null) {
									double advisePrice = strategy.getAdvicePrice();
									logger.debug("advisePrice=" + advisePrice);
									if (advisePrice > 0) {
										priceSet.add(advisePrice);
									}
								}
								continue;
							}
						}
					}
				}
			}
		} catch (Exception e) {
			logger.error("查找最低建议零售价发生错误=={}", e);
			return 0d;
		}
		logger.debug("tree set,advisePrice=" + JSON.toJSONString(priceSet));
		if (null != priceSet && priceSet.size() > 0) {
			Double min = (Double) priceSet.toArray()[0];
			logger.debug("查询通用产品最低建议零售价耗时:{}毫秒,返回最低建议零售价=={}", System.currentTimeMillis() - start, min);
			return min;
		}
		return 0d;
	}

	/**
	 * list为空时构造一个空list对象
	 *
	 * @param list
	 * @param <T>
	 * @return 一个以 RESPONSEBODY_KEY 为key,list为value的map作为responsebody
	 */
	private static <T> Map<String, List<T>> generateResponseBodyMap(List<T> list) {
		Map<String, List<T>> responseBodyMap = new HashMap<>();
		if (CollectionUtils.isEmpty(list)) {
			list = Lists.newArrayList();
		}
		responseBodyMap.put(RESPONSEBODY_KEY, list);
		return responseBodyMap;
	}

	@RequestMapping("getSlide")
	@ResponseBody
	public JsonEntity getSlide(HttpServletRequest request, @RequestAttribute JSONObject requestObject) throws Exception {
		List<HomeSlideConfig> result = recommendService.getHomeSlide(requestObject);
		Map<String, List<HomeSlideConfig>> map = new HashMap<>();
		map.put("slide", result);
		return JsonEntity.makeSuccessJsonEntity(map);
	}

	@RequestMapping("getClassify")
	@ResponseBody
	public JsonEntity getClassify(HttpServletRequest request, @RequestAttribute JSONObject requestObject) throws Exception {
		List<HomeClassificationConfig> result = recommendService.getHomeClassification(requestObject);
		Map<String, List<HomeClassificationConfig>> map = new HashMap<>();
		map.put("classify", result);
		return JsonEntity.makeSuccessJsonEntity(map);
	}

	@RequestMapping("getHotRec")
	@ResponseBody
	public JsonEntity getHotRecommend(@RequestAttribute JSONObject requestObject, @RequestAttribute Customer customer) throws Exception {
		List<HotProductModel> result = recommendService.getHotProduct(requestObject, customer);
		Map<String, List<HotProductModel>> map = new HashMap<>();
		map.put("hotRec", result);
		return JsonEntity.makeSuccessJsonEntity(map);
	}

}
