package com.shuishou.digitalmenu.menu.services;

import java.awt.Image;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.shuishou.digitalmenu.account.models.IUserDataAccessor;
import com.shuishou.digitalmenu.account.models.UserData;
import com.shuishou.digitalmenu.common.ConstantValue;
import com.shuishou.digitalmenu.common.models.IPrinterDataAccessor;
import com.shuishou.digitalmenu.common.models.Printer;
import com.shuishou.digitalmenu.log.models.LogData;
import com.shuishou.digitalmenu.log.services.ILogService;
import com.shuishou.digitalmenu.menu.models.Category1;
import com.shuishou.digitalmenu.menu.models.Category2;
import com.shuishou.digitalmenu.menu.models.Dish;
import com.shuishou.digitalmenu.menu.models.ICategory1DataAccessor;
import com.shuishou.digitalmenu.menu.models.ICategory2DataAccessor;
import com.shuishou.digitalmenu.menu.models.IDishDataAccessor;
import com.shuishou.digitalmenu.menu.models.IMenuVersionDataAccessor;
import com.shuishou.digitalmenu.menu.models.MenuVersion;
import com.shuishou.digitalmenu.menu.views.CheckMenuVersionResult;
import com.shuishou.digitalmenu.menu.views.GetCategory1Result;
import com.shuishou.digitalmenu.menu.views.GetCategory2Result;
import com.shuishou.digitalmenu.menu.views.GetDishResult;
import com.shuishou.digitalmenu.menu.views.GetMenuResult;
import com.shuishou.digitalmenu.menu.views.OperationResult;
import com.shuishou.digitalmenu.views.GridResult;
import com.shuishou.digitalmenu.views.Result;
import com.shuishou.digitalmenu.views.SimpleValueResult;

@Service
public class MenuService implements IMenuService {
	
	private final static Logger logger = Logger.getLogger(MenuService.class);
	
	@Autowired
	private ILogService logService;
	
	@Autowired
	private IUserDataAccessor userDA;
	
	@Autowired
	private ICategory1DataAccessor category1DA;
	
	@Autowired
	private ICategory2DataAccessor category2DA;
	
	@Autowired
	private IDishDataAccessor dishDA;
	
	@Autowired
	private HttpServletRequest request;
	
	@Autowired
	private IMenuVersionDataAccessor menuVersionDA;
	
	@Autowired
	private IPrinterDataAccessor printerDA;

	/**
	 * @param userId, the operator Id
	 */
	@Override
	@Transactional
	public OperationResult addCategory1(long userId, String chineseName, String englishName, int sequence) {
		Category1 c1 = new Category1();
		c1.setChineseName(chineseName);
		c1.setEnglishName(englishName);
		c1.setSequence(sequence);
		category1DA.save(c1);
		
		// write log.
		UserData selfUser = userDA.getUserById(userId);
		logService.write(selfUser, LogData.LogType.CATEGORY1_ADD.toString(), "User " + selfUser + " add " + c1);

		Map<String, String> infoMap = new HashMap<String, String>();
		infoMap.put("id", String.valueOf(c1.getId()));
		infoMap.put("type", ConstantValue.TYPE_CATEGORY1INFO);
		return new OperationResult(Result.OK, true, infoMap);
	}

	/**
	 * @param userId, the operator Id
	 */
	@Override
	@Transactional
	public OperationResult addCategory2(long userId, String chineseName, String englishName, int sequence, int category1Id, int printerId) {
		Category1 c1 = category1DA.getCategory1ById(category1Id);
		if (c1 == null){
			return new OperationResult("cannot find category1 by id : "+ category1Id, false, null);
		}
		Printer p = printerDA.getPrinterById(printerId);
		if (p == null){
			return new OperationResult("cannot find Printer by id : "+ printerId, false, null);
		}
		Category2 c2 = new Category2();
		c2.setChineseName(chineseName);
		c2.setEnglishName(englishName);
		c2.setSequence(sequence);
		c2.setCategory1(c1);
		c2.setPrinter(p);
		
		category2DA.save(c2);
		
		// write log.
		UserData selfUser = userDA.getUserById(userId);
		logService.write(selfUser, LogData.LogType.CATEGORY2_ADD.toString(), "User " + selfUser + " add " + c2);

		Map<String, String> infoMap = new HashMap<String, String>();
		infoMap.put("id", String.valueOf(c2.getId()));
		infoMap.put("type", ConstantValue.TYPE_CATEGORY2INFO);
		return new OperationResult(Result.OK, true, infoMap);
	}

	/**
	 * @param userId, the operator Id
	 */
	@Override
	@Transactional
	public OperationResult addDish(long userId, String chineseName, String englishName, int sequence, double price, boolean isNew, boolean isSpecial, int hotLevel, String abbreviation, MultipartFile image, int category2Id) {
		Category2 c2 = category2DA.getCategory2ById(category2Id);
		if (c2 == null){
			return new OperationResult("cannot find category2 by id "+ category2Id, false, null);
		}
		
		Map<String, String> resultInfoMap = new HashMap<String, String>();//return to client side
		resultInfoMap.put("type", ConstantValue.TYPE_DISHINFO);
		
		Dish dish = new Dish();
		dish.setChineseName(chineseName);
		dish.setEnglishName(englishName);
		dish.setSequence(sequence);
		dish.setCategory2(c2);
		dish.setPrice(price);
		dish.setNew(isNew);
		dish.setSpecial(isSpecial);
		dish.setAbbreviation(abbreviation);
		dishDA.save(dish);
		
		if (image != null && image.getSize() > 0){
			//save image as a file in server harddisk
			//generate a name for this dish. name formular: category1.englishname + '-' + category2.englishname + '-' + dish.englishname
			String fileName = "DISH-"+dish.getId() + "." + image.getOriginalFilename().substring(image.getOriginalFilename().lastIndexOf(".") + 1);
			try {
				dish.setPictureName(fileName);
				
				//generate small picture from original picture
				String fileNameBig = request.getSession().getServletContext().getRealPath("/")+"../" + ConstantValue.CATEGORY_DISHIMAGE_BIG + "/" + fileName;
				String fileNameSmall = request.getSession().getServletContext().getRealPath("/")+"../" + ConstantValue.CATEGORY_DISHIMAGE_SMALL + "/" + fileName;
				makeZoomImage(image.getInputStream(), fileNameBig, ConstantValue.DISHIMAGE_WIDTH_BIG, ConstantValue.DISHIMAGE_HEIGHT_BIG);
				makeZoomImage(image.getInputStream(), fileNameSmall, ConstantValue.DISHIMAGE_WIDTH_SMALL, ConstantValue.DISHIMAGE_HEIGHT_SMALL);
				resultInfoMap.put("dishicon", fileName);
			} catch (IOException e) {
				logger.error("Exception when create image file for dish : " + dish, e);
				e.printStackTrace();
			} 
		}
		
		// write log.
		UserData selfUser = userDA.getUserById(userId);
		logService.write(selfUser, LogData.LogType.DISH_ADD.toString(), "User " + selfUser + " add " + dish);
		
		resultInfoMap.put("id", String.valueOf(dish.getId()));
		
		return new OperationResult(Result.OK, true, resultInfoMap);
	}

	/**
	 * this can be used only for combobox, not for tree because 'id' is a resist word 
	 */
	@Override
	@Transactional
	public GetCategory1Result queryAllCategory1(){
		List<Category1> c1s = category1DA.getAllCategory1();
		List<GetCategory1Result.Category1Info> c1InfoList = new ArrayList<GetCategory1Result.Category1Info>();
		for (int i = 0; i < c1s.size(); i++) {
			GetCategory1Result.Category1Info c1info = new GetCategory1Result.Category1Info(
					c1s.get(i).getId(),
					c1s.get(i).getChineseName(), 
					c1s.get(i).getEnglishName(), 
					c1s.get(i).getSequence());
			c1InfoList.add(c1info);
		}
		return new GetCategory1Result(Result.OK,true, c1InfoList);
	}
	
	/**
	 * this can be used only for combobox, not for tree because 'id' is a resist word
	 */
	@Override
	@Transactional
	public GetCategory2Result queryAllCategory2(int category1Id){
		List<Category2> c2s = null;
		if (category1Id > 0){
			c2s = category2DA.getCategory2ByParent(category1Id);
		} else {
			c2s = category2DA.getAllCategory2();
		}
		List<GetCategory2Result.Category2Info> c2InfoList = null;
		if (c2s != null){
			c2InfoList = new ArrayList<GetCategory2Result.Category2Info>();
			for (int i = 0; i < c2s.size(); i++) {
				GetCategory2Result.Category2Info c2info = new GetCategory2Result.Category2Info(
						c2s.get(i).getId(),
						c2s.get(i).getChineseName(), 
						c2s.get(i).getEnglishName(), 
						c2s.get(i).getSequence(), 
						c2s.get(i).getCategory1().getId(),
						c2s.get(i).getPrinter() == null ? 0 : c2s.get(i).getPrinter().getId());
				c2InfoList.add(c2info);
			}
		}
		return new GetCategory2Result(Result.OK,true, c2InfoList);
	}
	
	@Override
	@Transactional
	public GetDishResult queryAllDish(int category2Id){
		List<Dish> dishes = null;
		if (category2Id > 0){
			dishes = dishDA.getDishesByParentId(category2Id);
		} else {
			dishes = dishDA.getDishesByParentId(category2Id);
		}
		List<GetDishResult.DishInfo> dishInfoList = null;
		if (dishes != null){
			dishInfoList = new ArrayList<GetDishResult.DishInfo>();
			for (int i = 0; i < dishes.size(); i++) {
				GetDishResult.DishInfo dishinfo = new GetDishResult.DishInfo();
				dishinfo.id = dishes.get(i).getId();
				dishinfo.chineseName = dishes.get(i).getChineseName();
				dishinfo.englishName = dishes.get(i).getEnglishName();
				dishinfo.sequence = dishes.get(i).getSequence();
				dishinfo.price = dishes.get(i).getPrice();
				dishinfo.sequence = dishes.get(i).getSequence();
				dishInfoList.add(dishinfo);
			}
			
		}
		return new GetDishResult(Result.OK, true, dishInfoList);
	}
	
	@Override
	@Transactional
	public GetDishResult queryDishById(int dishId){
		Dish dish = dishDA.getDishById(dishId);
		if (dish == null)
			return new GetDishResult("cannot find dish by id "+ dishId, false, null);
		List<GetDishResult.DishInfo> dishInfoList = null;
		if (dish != null){
			dishInfoList = new ArrayList<GetDishResult.DishInfo>();
			GetDishResult.DishInfo dishinfo = new GetDishResult.DishInfo();
			dishinfo.id = dish.getId();
			dishinfo.chineseName = dish.getChineseName();
			dishinfo.englishName = dish.getEnglishName();
			dishinfo.sequence = dish.getSequence();
			dishinfo.price = dish.getPrice();
			dishinfo.sequence = dish.getSequence();
			dishinfo.isSoldOut = dish.isSoldOut();
			dishInfoList.add(dishinfo);
		}
		return new GetDishResult(Result.OK, true, dishInfoList);
	}
	
	private GetMenuResult queryCategory2ByCategory1Id(int c1Id){
		Category1 c1 = category1DA.getCategory1ById(c1Id);
		if (c1 == null)
			return new GetMenuResult("cannot get category1 by id "+ c1Id, false, null);
		List<Category2> c2s = c1.getCategory2s();
		List<GetMenuResult.Category2Info> c2InfoList = new ArrayList<GetMenuResult.Category2Info>();
		for (int j = 0; j < c2s.size(); j++) {
			GetMenuResult.Category2Info c2info = new GetMenuResult.Category2Info(
					c2s.get(j).getId(),
					c2s.get(j).getChineseName(), 
					c2s.get(j).getEnglishName(), 
					c2s.get(j).getSequence(), 
					c1.getId(),
					new ArrayList<GetMenuResult.DishInfo>(),
					c2s.get(j).getPrinter() == null ? 0 : c2s.get(j).getPrinter().getId() );
			if (c2s.get(j).getDishes() == null || c2s.get(j).getDishes().isEmpty())
				c2info.loaded = true;
			c2InfoList.add(c2info);
		}
		return new GetMenuResult(Result.OK, true, c2InfoList);
	}
	
	private GetMenuResult queryDishByCategory2Id(int c2Id){
		Category2 c2 = category2DA.getCategory2ById(c2Id);
		if (c2 == null)
			return new GetMenuResult("cannot get category2 by id "+ c2Id, false, null);
		List<Dish> dishes = c2.getDishes();
		List<GetMenuResult.DishInfo> dishInfoList = new ArrayList<GetMenuResult.DishInfo>();
		for (int k = 0; k < dishes.size(); k++) {
			GetMenuResult.DishInfo dishInfo = new GetMenuResult.DishInfo();
			dishInfo.objectid = dishes.get(k).getId();
			dishInfo.chineseName = dishes.get(k).getChineseName();
			dishInfo.englishName = dishes.get(k).getEnglishName();
			dishInfo.sequence = dishes.get(k).getSequence();
			dishInfo.price = dishes.get(k).getPrice();
			dishInfo.parentID = c2.getId();
			dishInfo.isNew = dishes.get(k).isNew();
			dishInfo.isSpecial = dishes.get(k).isSpecial();
			dishInfo.hotLevel = dishes.get(k).getHotLevel();
			dishInfo.pictureName = dishes.get(k).getPictureName();
			dishInfo.displayText = dishInfo.chineseName;
			dishInfoList.add(dishInfo);
		}
		return new GetMenuResult(Result.OK, true, dishInfoList);
	}
	
	@Override
	@Transactional
	public GetMenuResult queryMenu(String node) {
		if ("root".equals(node) || "NaN".equals(node)){
//			return queryAllCategory1();
		} else if (node.startsWith("C1")){
			return queryCategory2ByCategory1Id(Integer.parseInt(node.substring(3)));
		} else if (node.startsWith("C2")){
			return queryDishByCategory2Id(Integer.parseInt(node.substring(3)));
		}
		return new GetMenuResult("wrong node", false, null);
	}
	
	@Override
	@Transactional
	public GetMenuResult queryAllMenu() {
//		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		logger.info("start query menu " + ConstantValue.DFYMDHMS.format(System.currentTimeMillis()));
		List<Category1> c1s = category1DA.getAllCategory1();
		logger.info("finish query category1");
//		c1s.sort(new Comparator<Object>(){
//			@Override
//			public int compare(Object o1, Object o2) {
//				if (o1 instanceof Category1 && o2 instanceof Category1){
//					return ((Category1)o1).getSequence() - ((Category1)o2).getSequence();
//				}
//				return 0;
//			}
//		});
		List<GetMenuResult.Category1Info> c1InfoList = new ArrayList<GetMenuResult.Category1Info>();
		for (int i = 0; i < c1s.size(); i++) {
			GetMenuResult.Category1Info c1info = new GetMenuResult.Category1Info(
					c1s.get(i).getId(),
					c1s.get(i).getChineseName(), 
					c1s.get(i).getEnglishName(), 
					c1s.get(i).getSequence(), 
					new ArrayList<GetMenuResult.Category2Info>());
			//if (c1s.get(i).getCategory2s() == null || c1s.get(i).getCategory2s().isEmpty())
				c1info.loaded = true;
			c1InfoList.add(c1info);
			List<Category2> c2s = c1s.get(i).getCategory2s();
			logger.info("query once category2 in C1:" + c1s.get(i).getChineseName());
//			c2s.sort(new Comparator<Object>(){
//				@Override
//				public int compare(Object o1, Object o2) {
//					if (o1 instanceof Category2 && o2 instanceof Category2){
//						return ((Category2)o1).getSequence() - ((Category2)o2).getSequence();
//					}
//					return 0;
//				}
//			});
			for (int j = 0; j < c2s.size(); j++) {
				GetMenuResult.Category2Info c2info = new GetMenuResult.Category2Info(
						c2s.get(j).getId(),
						c2s.get(j).getChineseName(), 
						c2s.get(j).getEnglishName(), 
						c2s.get(j).getSequence(), 
						c1s.get(i).getId(),
						new ArrayList<GetMenuResult.DishInfo>(),
						c2s.get(j).getPrinter() == null ? 0 : c2s.get(j).getPrinter().getId());
				//if (c2s.get(j).getDishes() == null || c2s.get(j).getDishes().isEmpty())
					c2info.loaded = true;
				c1info.children.add(c2info);
				List<Dish> dishes = c2s.get(j).getDishes();
				logger.info("query once dish in C2:" + c2s.get(j).getChineseName());
//				dishes.sort(new Comparator<Object>(){
//					@Override
//					public int compare(Object o1, Object o2) {
//						if (o1 instanceof Dish && o2 instanceof Dish){
//							return ((Dish)o1).getSequence() - ((Dish)o2).getSequence();
//						}
//						return 0;
//					}
//				});
				for (int k = 0; k < dishes.size(); k++) {
					GetMenuResult.DishInfo dishInfo = new GetMenuResult.DishInfo();
					dishInfo.objectid = dishes.get(k).getId();
					dishInfo.chineseName = dishes.get(k).getChineseName();
					dishInfo.englishName = dishes.get(k).getEnglishName();
					dishInfo.abbreviation = dishes.get(k).getAbbreviation();
					dishInfo.sequence = dishes.get(k).getSequence();
					dishInfo.price = dishes.get(k).getPrice();
					dishInfo.parentID = c2s.get(j).getId();
					dishInfo.isNew = dishes.get(k).isNew();
					dishInfo.isSpecial = dishes.get(k).isSpecial();
					dishInfo.isSoldOut = dishes.get(k).isSoldOut();
					dishInfo.hotLevel = dishes.get(k).getHotLevel();
					dishInfo.pictureName = dishes.get(k).getPictureName();
					dishInfo.displayText = dishInfo.chineseName;
					if (dishInfo.isNew)
						dishInfo.displayText += "<html><font color=red>[NEW]</font></html>";
					if (dishInfo.isSpecial)
						dishInfo.displayText += "<html><font color=red>[SPECIAL]</font></html>";
					if (dishInfo.isSoldOut)
						dishInfo.displayText += "<html><font color=blue>[SOLDOUT]</font></html>";
//					String picname = dishes.get(k).getPictureName();
//					if (picname != null)
//						dishInfo.icon = "../"+picname.replace(ConstantValue.CATEGORY_DISHIMAGE_ORIGINAL, ConstantValue.CATEGORY_DISHIMAGE_SMALL);
					c2info.children.add(dishInfo);
				}
			}
		}
		logger.info("finish query menu");
		return new GetMenuResult(Result.OK,true, c1InfoList);
	}

	@Override
	@Transactional
	public Result deleteCategory1(long userId, int category1Id) {
		Category1 c1 = category1DA.getCategory1ById(category1Id);
		if (c1 == null)
			return new Result("now found Category1 by id "+ category1Id);
		if (c1.getCategory2s() != null && !c1.getCategory2s().isEmpty())
			return new Result("this category is not empty");
		category1DA.delete(c1);
		// write log.
		UserData selfUser = userDA.getUserById(userId);
		logService.write(selfUser, LogData.LogType.CATEGORY1_DELETE.toString(),
				"User " + selfUser + " delete Category1 " + c1 + ".");

		return new Result(Result.OK);	
	}

	@Override
	@Transactional
	public Result deleteCategory2(long userId, int category2Id) {
		Category2 c2 = category2DA.getCategory2ById(category2Id);
		if (c2 == null)
			return new Result("now found Category2 by id "+ category2Id);
		if (c2.getDishes() != null && !c2.getDishes().isEmpty())
			return new Result("this category is not empty");
		category2DA.delete(c2);
		// write log.
		UserData selfUser = userDA.getUserById(userId);
		logService.write(selfUser, LogData.LogType.CATEGORY2_DELETE.toString(),
				"User " + selfUser + " delete Category2 " + c2 + ".");

		return new Result(Result.OK);		
	}

	@Override
	@Transactional
	public Result deleteDish(long userId, int dishId) {
		Dish dish = dishDA.getDishById(dishId);
		if (dish == null)
			return new Result("not found dish by ID " + dishId);
		dishDA.delete(dish);

		//delete picture files
		String filePath = request.getSession().getServletContext().getRealPath("/")+"../";
		String fileName = dish.getPictureName();
		if (fileName != null) {
			File file = new File(filePath + ConstantValue.CATEGORY_DISHIMAGE_BIG + "/" +fileName);
			if (file.exists())
				file.delete();
			file = new File(filePath + ConstantValue.CATEGORY_DISHIMAGE_SMALL + "/" + fileName);
			if (file.exists())
				file.delete();
		}
		
		// write log.
		UserData selfUser = userDA.getUserById(userId);
		logService.write(selfUser, LogData.LogType.DISH_DELETE.toString(),
				"User " + selfUser + " delete Dish " + dish + ".");

		return new Result(Result.OK);
	}

	@Override
	@Transactional
	public OperationResult updateCategory1(long userId, int id, String chineseName, String englishName, int sequence) {
		Category1 c1 = category1DA.getCategory1ById(id);
		if (c1 == null)
			return new OperationResult("now found Category1 by id "+ id, false, null);
		c1.setChineseName(chineseName);
		c1.setEnglishName(englishName);
		c1.setSequence(sequence);
		category1DA.save(c1);
		
		// write log.
		UserData selfUser = userDA.getUserById(userId);
		logService.write(selfUser, LogData.LogType.CATEGORY1_MODIFY.toString(),
				"User " + selfUser + " update Category1, id = " + id 
				+ ", chineseName = " + chineseName + ", englishName = "
				+englishName + ", sequence = "+sequence+".");
		return new OperationResult(Result.OK, true, null);
	}

	@Override
	@Transactional
	public OperationResult updateCategory2(long userId, int id, String chineseName, String englishName, int sequence,
			int category1Id, int printerId) {
		Category1 c1 = category1DA.getCategory1ById(category1Id);
		if (c1 == null)
			return new OperationResult("now found Category1 by id "+ category1Id, false, null);
		Category2 c2 = category2DA.getCategory2ById(id);
		if (c2 == null)
			return new OperationResult("now found Category2 by id "+ id, false, null);
		Printer p = printerDA.getPrinterById(printerId);
		if (p == null){
			return new OperationResult("cannot find Printer by id : "+ printerId, false, null);
		}
		c2.setChineseName(chineseName);
		c2.setEnglishName(englishName);
		c2.setSequence(sequence);
		c2.setCategory1(c1);
		c2.setPrinter(p);
		category2DA.save(c2);
		
		// write log.
		UserData selfUser = userDA.getUserById(userId);
		logService.write(selfUser, LogData.LogType.CATEGORY2_MODIFY.toString(),
				"User " + selfUser + " update Category2, id = " + id 
				+ ", chineseName = " + chineseName + ", englishName = "
				+englishName + ", sequence = "+sequence+", Category1 = "+ c1+".");
		return new OperationResult(Result.OK, true, null);
	}

	@Override
	@Transactional
	public OperationResult updateDish(long userId, int id, String chineseName, String englishName, int sequence, double price, boolean isNew, boolean isSpecial, byte hotLevel, String abbreviation, int category2Id) {
		Category2 c2 = category2DA.getCategory2ById(category2Id);
		if (c2 == null)
			return new OperationResult("now found Category2 by id "+ category2Id, false, null);
		Dish dish = dishDA.getDishById(id);
		if (dish == null)
			return new OperationResult("now found dish by id "+ id, false, null);
		dish.setCategory2(c2);
		dish.setChineseName(chineseName);
		dish.setEnglishName(englishName);
		dish.setPrice(price);
		dish.setSequence(sequence);
		dish.setNew(isNew);
		dish.setSpecial(isSpecial);
		dish.setHotLevel(hotLevel);
		dish.setAbbreviation(abbreviation);
		dishDA.save(dish);
		
		// write log.
		UserData selfUser = userDA.getUserById(userId);
		logService.write(selfUser, LogData.LogType.DISH_MODIFY.toString(),
				"User " + selfUser + " update Category2, id = " + id 
				+ ", chineseName = " + chineseName + ", englishName = "
				+englishName + ", sequence = "+sequence+", price = " + price + ", Category2 = "+ c2+".");
		return new OperationResult(Result.OK, true, null);		
	}
	
	/*
     * 图片缩放,w，h为缩放的目标宽度和高度
     * src为源文件目录，dest为缩放后保存目录
     * 如果图片原始尺寸小于设定尺寸, 不进行缩放 
     */
    public static void makeZoomImage(BufferedImage bufImg,String dest,int w,int h) throws IOException {
        
        double wr=0,hr=0;
        File destFile = new File(dest);

        Image Itemp = bufImg.getScaledInstance(w, h, Image.SCALE_SMOOTH);//设置缩放目标图片模板
        
        wr=w*1.0 / bufImg.getWidth();     //获取缩放比例
        hr=h*1.0 / bufImg.getHeight();

        if(wr > 1.0){
        	wr = 1.0;
        }
        if (hr > 1.0){
        	hr = 1.0;
        }
        if (wr > hr){
        	wr = hr;
        } else {
        	hr = wr;
        }
        
        AffineTransformOp ato = new AffineTransformOp(AffineTransform.getScaleInstance(wr, hr), null);
        Itemp = ato.filter(bufImg, null);
        
        ImageIO.write((BufferedImage) Itemp,dest.substring(dest.lastIndexOf(".")+1), destFile); //写入缩减后的图片
        
    }
	/*
     * 图片缩放,w，h为缩放的目标宽度和高度
     * src为源文件目录，dest为缩放后保存目录
     * 如果图片原始尺寸小于设定尺寸, 不进行缩放 
     */
    public static void makeZoomImage(InputStream is,String dest,int w,int h) throws IOException {
    	BufferedImage bufImg = ImageIO.read(is); //读取图片
    	makeZoomImage(bufImg, dest, w, h);
    }
	
	/*
     * 图片缩放,w，h为缩放的目标宽度和高度
     * src为源文件目录，dest为缩放后保存目录
     * 如果图片原始尺寸小于设定尺寸, 不进行缩放 
     */
    public static void makeZoomImage(String src,String dest,int w,int h) throws IOException {
        File srcFile = new File(src);
        BufferedImage bufImg = ImageIO.read(srcFile); //读取图片
        makeZoomImage(bufImg, dest, w, h);
    }
    
    /*
     * 图片按比率缩放
     * size为文件大小
     */
    public static void makeZoomImage(String src,String dest,Integer size) throws IOException {
        File srcFile = new File(src);
        File destFile = new File(dest);
        
        long fileSize = srcFile.length();
        if(fileSize < size * 1024)   //文件大于size k时，才进行缩放
            return;
        
        Double rate = (size * 1024 * 0.5) / fileSize; // 获取长宽缩放比例
        
        BufferedImage bufImg = ImageIO.read(srcFile);
        Image Itemp = bufImg.getScaledInstance(bufImg.getWidth(), bufImg.getHeight(), Image.SCALE_SMOOTH);
            
        AffineTransformOp ato = new AffineTransformOp(AffineTransform.getScaleInstance(rate, rate), null);
        Itemp = ato.filter(bufImg, null);
        
        ImageIO.write((BufferedImage) Itemp,dest.substring(dest.lastIndexOf(".")+1), destFile);
        
    }

	@Override
	@Transactional
	public OperationResult changeDishPrice(long userId, int id, double newprice) {
		Dish dish = dishDA.getDishById(id);
		if (dish == null)
			return new OperationResult("now found dish by id "+ id, false, null);
		double oldprice = dish.getPrice();
		dish.setPrice(newprice);
		dishDA.save(dish);
		
		// write log.
		UserData selfUser = userDA.getUserById(userId);
		logService.write(selfUser, LogData.LogType.DISH_MODIFY.toString(),
				"User " + selfUser + " change dish price, id = " + id 
				+ ", old price = " + oldprice + ", new price = " + newprice +".");
		return new OperationResult(Result.OK, true, null);		
	}

	@Override
	@Transactional
	public OperationResult changeDishPicture(long userId, int id, MultipartFile image) {
		Dish dish = dishDA.getDishById(id);
		if (dish == null)
			return new OperationResult("now found dish by id "+ id, false, null);
		Map<String, String> resultInfoMap = new HashMap<String, String>();//return to client side
		if (image != null && image.getSize() > 0){
			//save image as a file in server harddisk
			//generate a name for this dish. name formular: category1.englishname + '-' + category2.englishname + '-' + dish.englishname
			String filePath = request.getSession().getServletContext().getRealPath("/")+"..\\" + ConstantValue.CATEGORY_DISHIMAGE_BIG + "/";
			String fileName = null;
			if (dish.getPictureName() != null){
				File file = new File(filePath + fileName);
				if (file.exists())
					file.delete();
//				//如果fileName已经存在, 就生成一个新的文件名. 如果继续使用旧文件名, 浏览器不会去下载更新后的图片
//				int random = (int)(Math.random() * 1000);
//				fileName = dish.getPictureName();
//				fileName = "DISH-"+dish.getId() + "-" + random + fileName.substring(fileName.length() - 4);
			} 

			////重新生成文件名, 避免扩展名不一致的情况
			fileName = "DISH-"+dish.getId() + "." + image.getOriginalFilename().substring(image.getOriginalFilename().lastIndexOf(".") + 1);
		
			dish.setPictureName(fileName);
			dishDA.update(dish);
			
			try {
				//generate small picture from original picture
				String fileNameBig = request.getSession().getServletContext().getRealPath("/")+"../" + ConstantValue.CATEGORY_DISHIMAGE_BIG + "/" + fileName;
				String fileNameSmall = request.getSession().getServletContext().getRealPath("/")+"../" + ConstantValue.CATEGORY_DISHIMAGE_SMALL + "/" + fileName;
				makeZoomImage(image.getInputStream(), fileNameBig, ConstantValue.DISHIMAGE_WIDTH_BIG, ConstantValue.DISHIMAGE_HEIGHT_BIG);
				makeZoomImage(image.getInputStream(), fileNameSmall, ConstantValue.DISHIMAGE_WIDTH_SMALL, ConstantValue.DISHIMAGE_HEIGHT_SMALL);
				resultInfoMap.put("dishicon", fileName);
			} catch (IOException e) {
				logger.error("Exception when create image file for dish : " + dish, e);
				e.printStackTrace();
			} 
			
			// write log.
			UserData selfUser = userDA.getUserById(userId);
			logService.write(selfUser, LogData.LogType.DISH_MODIFY.toString(),
					"User " + selfUser + " change dish picture, id = " + id +".");
		}
		return new OperationResult(Result.OK, true, resultInfoMap);
	}

	@Override
	@Transactional
	public Result changeDishSpecial(long userId, int id, boolean isSpecial) {
		Dish dish = dishDA.getDishById(id);
		if (dish == null)
			return new Result("now found dish by id "+ id);
		dish.setSpecial(isSpecial);
		dishDA.save(dish);
		
		// write log.
		UserData selfUser = userDA.getUserById(userId);
		logService.write(selfUser, LogData.LogType.DISH_MODIFY.toString(),
				"User " + selfUser + " change dish special as " + isSpecial + ".");
		return new Result(Result.OK);	
	}

	@Override
	@Transactional
	public Result changeDishNewProduct(long userId, int id, boolean isNew) {
		Dish dish = dishDA.getDishById(id);
		if (dish == null)
			return new Result("now found dish by id "+ id);
		dish.setNew(isNew);
		dishDA.save(dish);
		
		// write log.
		UserData selfUser = userDA.getUserById(userId);
		logService.write(selfUser, LogData.LogType.DISH_MODIFY.toString(),
				"User " + selfUser + " change dish isNew as " + isNew + ".");
		return new Result(Result.OK);
	}
	
	@Override
	@Transactional
	public Result changeDishSoldOut(long userId, int id, boolean isSoldOut) {
		Dish dish = dishDA.getDishById(id);
		if (dish == null)
			return new Result("now found dish by id "+ id);
		dish.setSoldOut(isSoldOut);
		dishDA.save(dish);
		
		//add record to menu_version
		MenuVersion mv = new MenuVersion();
		mv.setDishId(id);
		mv.setType(ConstantValue.MENUCHANGE_TYPE_SOLDOUT);//不管是sold out还是取消sold out, 都使用这个值
		menuVersionDA.save(mv);
		
		// write log.
		UserData selfUser = userDA.getUserById(userId);
		logService.write(selfUser, LogData.LogType.DISH_MODIFY.toString(),
				"User " + selfUser + " change dish Sold Out as " + isSoldOut + ".");
		return new Result(Result.OK);
	}

	@Override
	@Transactional
	public CheckMenuVersionResult checkMenuVersion(int versionId){
		List<MenuVersion> mvs = menuVersionDA.getMenuVersionFromId(versionId);
		if (mvs == null || mvs.isEmpty())
			return new CheckMenuVersionResult(Result.OK, true, null);
		List<CheckMenuVersionResult.MenuVersionInfo> infos = new ArrayList<CheckMenuVersionResult.MenuVersionInfo>();
		for (int i = 0; i < mvs.size(); i++) {
			CheckMenuVersionResult.MenuVersionInfo info = new CheckMenuVersionResult.MenuVersionInfo();
			info.id = mvs.get(i).getId();
			info.dishId = mvs.get(i).getDishId();
			info.type = mvs.get(i).getType();
			infos.add(info);
		}
		return new CheckMenuVersionResult(Result.OK, true, infos);
	}

	@Override
	@Transactional
	public SimpleValueResult getlastMenuVersion() {
		MenuVersion mv = menuVersionDA.getLastRecord();
		int version = 0;
		if (mv != null)
			version = mv.getId();
		return new SimpleValueResult(Result.OK, true, version+"");
	}
	
}
