#数据更新原因:
#配置产品热门推荐
#需更新环境: 
#预发布
#操作的数据库:
#appapi
#1、查询数据
SELECT * from appapi`.`hot_product_recommend`;
#2、执行语句
ALTER TABLE `hot_product_recommend` CHANGE `product_type` `product_type` INT(5)  NOT NULL  COMMENT '产品类型(参照：http://10.0.18.11:8080/pages/viewpage.action?pageId=6360116)';
INSERT INTO `appapi`.`hot_product_recommend` (app_type, province, product_id, product_type,seq)SELECT 0, province, id, product_type,1 FROM `core_deploment`.`product_info` WHERE province != "" AND flag = 1 AND product_type IN(1000,4001,4002,5000,7000,9000);
INSERT INTO `appapi`.`hot_product_recommend` (app_type, province, product_id, product_type,seq) SELECT 0, province, id, IF(TYPE = 1,1,IF(TYPE = 2,10,0)),IF(TYPE = 1,3,IF(TYPE = 2,2,0)) FROM `core_deploment`.`product_scenic` WHERE province != "" AND status = 1 and  TYPE IN(1,2);
#3、验证数据
SELECT * from appapi`.`hot_product_recommend`;
#4、回滚数据
TRUNCATE TABLE `hot_product_recommend`;