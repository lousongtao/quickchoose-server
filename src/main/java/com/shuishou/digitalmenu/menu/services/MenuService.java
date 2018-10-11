package com.shuishou.digitalmenu.menu.services;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.Transparency;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.hibernate.Hibernate;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.shuishou.digitalmenu.ConstantValue;
import com.shuishou.digitalmenu.account.models.IUserDataAccessor;
import com.shuishou.digitalmenu.account.models.UserData;
import com.shuishou.digitalmenu.common.models.IPrinterDataAccessor;
import com.shuishou.digitalmenu.common.models.Printer;
import com.shuishou.digitalmenu.log.models.LogData;
import com.shuishou.digitalmenu.log.services.ILogService;
import com.shuishou.digitalmenu.menu.models.Category1;
import com.shuishou.digitalmenu.menu.models.Category2;
import com.shuishou.digitalmenu.menu.models.Category2Printer;
import com.shuishou.digitalmenu.menu.models.Dish;
import com.shuishou.digitalmenu.menu.models.DishChoosePopinfo;
import com.shuishou.digitalmenu.menu.models.DishConfig;
import com.shuishou.digitalmenu.menu.models.DishConfigGroup;
import com.shuishou.digitalmenu.menu.models.Flavor;
import com.shuishou.digitalmenu.menu.models.ICategory1DataAccessor;
import com.shuishou.digitalmenu.menu.models.ICategory2DataAccessor;
import com.shuishou.digitalmenu.menu.models.ICategory2PrinterDataAccessor;
import com.shuishou.digitalmenu.menu.models.IDishChoosePopinfoDataAccessor;
import com.shuishou.digitalmenu.menu.models.IDishConfigDataAccessor;
import com.shuishou.digitalmenu.menu.models.IDishConfigGroupDataAccessor;
import com.shuishou.digitalmenu.menu.models.IDishDataAccessor;
import com.shuishou.digitalmenu.menu.models.IDishMaterialConsumeDataAccessor;
import com.shuishou.digitalmenu.menu.models.IFlavorDataAccessor;
import com.shuishou.digitalmenu.menu.models.IMenuVersionDataAccessor;
import com.shuishou.digitalmenu.menu.models.MenuVersion;
import com.shuishou.digitalmenu.menu.views.CheckMenuVersionResult;
import com.shuishou.digitalmenu.rawmaterial.models.IMaterialDataAccessor;
import com.shuishou.digitalmenu.views.ObjectListResult;
import com.shuishou.digitalmenu.views.ObjectResult;
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
	private IMaterialDataAccessor materialDA;
	
	@Autowired
	private IDishMaterialConsumeDataAccessor dishMaterialConsumeDA;
	
	@Autowired
	private IDishChoosePopinfoDataAccessor dishChoosePopinfoDA;
	
	@Autowired
	private HttpServletRequest request;
	
	@Autowired
	private IMenuVersionDataAccessor menuVersionDA;
	
	@Autowired
	private IPrinterDataAccessor printerDA;
	
	@Autowired
	private IFlavorDataAccessor flavorDA;
	
	@Autowired
	private ICategory2PrinterDataAccessor category2PrinterDA;
	
	@Autowired
	private IDishConfigDataAccessor dishConfigDA;
	
	@Autowired
	private IDishConfigGroupDataAccessor dishConfigGroupDA;
	

	@Override
	@Transactional
	public ObjectResult addFlavor(int userId, String firstLanguageName, String secondLanguageName){
		Flavor flavor = new Flavor();
		flavor.setFirstLanguageName(firstLanguageName);
		flavor.setSecondLanguageName(secondLanguageName);
		flavorDA.save(flavor);
		
		// write log.
		UserData selfUser = userDA.getUserById(userId);
		logService.write(selfUser, LogData.LogType.FLAVOR_CHANGE.toString(), "User " + selfUser + " add flavor : " + flavor);
		return new ObjectResult(Result.OK, true, flavor);
	}
	
	
	/**
	 * @param userId, the operator Id
	 */
	@Override
	@Transactional
	public ObjectResult addCategory1(int userId, String firstLanguageName, String secondLanguageName, int sequence) {
		Category1 c1 = new Category1();
		c1.setFirstLanguageName(firstLanguageName);
		c1.setSecondLanguageName(secondLanguageName);
		c1.setSequence(sequence);
		category1DA.save(c1);
		hibernateInitialCategory1(c1);
		
		//add record to menu_version
		MenuVersion mv = new MenuVersion();
		mv.setObjectId(c1.getId());
		mv.setType(ConstantValue.MENUCHANGE_TYPE_CATEGORY1ADD);
		menuVersionDA.save(mv);
				
		// write log.
		UserData selfUser = userDA.getUserById(userId);
		logService.write(selfUser, LogData.LogType.CATEGORY1_CHANGE.toString(), "User " + selfUser 
				+ " add Category1 : firstLanguageName =" + firstLanguageName
				+ ", secondLanguageName = " + secondLanguageName
				+ ", sequence = " + sequence);

		return new ObjectResult(Result.OK, true, c1);
	}

	/**
	 * @param userId, the operator Id
	 */
	@Override
	@Transactional
	public ObjectResult addCategory2(int userId, String firstLanguageName, String secondLanguageName, int sequence, int category1Id, JSONArray jaPrinter) {
		Category1 c1 = category1DA.getCategory1ById(category1Id);
		if (c1 == null){
			return new ObjectResult("cannot find category1 by id : "+ category1Id, false, null);
		}
		
		Category2 c2 = new Category2();
		c2.setFirstLanguageName(firstLanguageName);
		c2.setSecondLanguageName(secondLanguageName);
		c2.setSequence(sequence);
		c2.setCategory1(c1);
		category2DA.save(c2);
		for (int i = 0; i < jaPrinter.length(); i++) {
			JSONObject joPrinter = (JSONObject) jaPrinter.get(i);
			int printerId = joPrinter.getInt("printerId");
			int printStyle = joPrinter.getInt("printStyle");
			Printer p = printerDA.getPrinterById(printerId);
			if (p == null){
				return new ObjectResult("cannot find Printer by id : "+ printerId, false, null);
			}
			Category2Printer cp = new Category2Printer();
			cp.setPrinter(p);
			cp.setCategory2(c2);
			cp.setPrintStyle(printStyle);
			c2.addCategory2Printer(cp);
			category2PrinterDA.save(cp);
		}
		
		hibernateInitialCategory2(c2);

		//add record to menu_version
		MenuVersion mv = new MenuVersion();
		mv.setObjectId(c2.getId());
		mv.setType(ConstantValue.MENUCHANGE_TYPE_CATEGORY2ADD);
		menuVersionDA.save(mv);
		
		// write log.
		UserData selfUser = userDA.getUserById(userId);
		logService.write(selfUser, LogData.LogType.CATEGORY2_CHANGE.toString(), "User " + selfUser + " add Category2 : firstLanguageName = " + firstLanguageName
				+ ", secondLanguageName = " + secondLanguageName + ", sequence = " + sequence + ", category1 = " + c1.getFirstLanguageName());

		return new ObjectResult(Result.OK, true, c2);
	}

	/**
	 * @param userId, the operator Id
	 */
	@Override
	@Transactional
	public ObjectResult addDish(int userId, String firstLanguageName, String secondLanguageName, int sequence, 
			double price, boolean isNew, boolean isSpecial, int hotLevel, String abbreviation,  MultipartFile image, int category2Id, int chooseMode, 
			DishChoosePopinfo popinfo, boolean autoMerge, int purchaseType, boolean allowFlavor, String description_1stlang, String description_2ndlang) {
		Category2 c2 = category2DA.getCategory2ById(category2Id);
		if (c2 == null){
			return new ObjectResult("cannot find category2 by id "+ category2Id, false, null);
		}
		//check data consistency
		if (chooseMode == ConstantValue.DISH_CHOOSEMODE_POPINFOCHOOSE || chooseMode == ConstantValue.DISH_CHOOSEMODE_POPINFOQUIT){
			if (popinfo == null){
				return new ObjectResult("no pop info", false, null);
			}
		}
		
		Dish dish = new Dish();
		dish.setFirstLanguageName(firstLanguageName);
		dish.setSecondLanguageName(secondLanguageName);
		dish.setSequence(sequence);
		dish.setCategory2(c2);
		dish.setPrice(price);
		dish.setHotLevel(hotLevel);
		dish.setNew(isNew);
		dish.setSpecial(isSpecial);
		dish.setAbbreviation(abbreviation);
		dish.setChooseMode(chooseMode);
		dish.setAutoMergeWhileChoose(autoMerge);
		dish.setPurchaseType(purchaseType);
		dish.setAllowFlavor(allowFlavor);
		dish.setDescription_1stlang(description_1stlang);
		dish.setDescription_2ndlang(description_2ndlang);
		dishDA.save(dish);
		
		//add record to menu_version
		MenuVersion mv = new MenuVersion();
		mv.setObjectId(dish.getId());
		mv.setType(ConstantValue.MENUCHANGE_TYPE_DISHADD);
		menuVersionDA.save(mv);
//				
		if (chooseMode == ConstantValue.DISH_CHOOSEMODE_POPINFOCHOOSE 
				|| chooseMode == ConstantValue.DISH_CHOOSEMODE_POPINFOQUIT){
			popinfo.setDish(dish);
			dishChoosePopinfoDA.save(popinfo);
			dish.setChoosePopInfo(popinfo);
		}
		
		// write log.
		UserData selfUser = userDA.getUserById(userId);
		logService.write(selfUser, LogData.LogType.DISH_CHANGE.toString(), "User " + selfUser + " add Dish : firstLanguageName = " + firstLanguageName
				+ ", secondLanguageName = "+ secondLanguageName);
		
		hibernateInitialDish(dish);
		return new ObjectResult(Result.OK, true, dish);
	}
	
	@Override
	@Transactional
	public ObjectResult queryDishById(int dishId){
		Dish dish = dishDA.getDishById(dishId);
		if (dish == null)
			return new ObjectResult("cannot find dish by id "+ dishId, false, null);
		hibernateInitialDish(dish);
		return new ObjectResult(Result.OK, true, dish);
	}
	
	@Override
	@Transactional
	public ObjectListResult queryDishByIdList(ArrayList<Integer> dishIdList){
		ArrayList<Dish> dishes = new ArrayList<>();
		for(Integer i : dishIdList){
			Dish dish = dishDA.getDishById(i);
			if (dish == null)
				return new ObjectListResult("cannot find dish by id "+ i, false, null);
			hibernateInitialDish(dish);
			dishes.add(dish);
		}
		return new ObjectListResult(Result.OK, true, dishes);
	}
	
	@Override
	@Transactional
	public ObjectListResult queryDishConfigByIdList(ArrayList<Integer> dishConfigIdList){
		ArrayList<DishConfig> dishConfigList = new ArrayList<>();
		for(Integer i : dishConfigIdList){
			DishConfig config = dishConfigDA.getDishConfigById(i);
			if (config == null)
				return new ObjectListResult("cannot find dish config by id "+ i, false, null);
			dishConfigList.add(config);
		}
		return new ObjectListResult(Result.OK, true, dishConfigList);
	}
	
	@Override
	@Transactional
	public ObjectResult queryCategory1ById(int id){
		Category1 c1 = category1DA.getCategory1ById(id);
		if (c1 == null)
			return new ObjectResult("cannot find Category1 by id "+ id, false, null);
		hibernateInitialCategory1(c1);
		return new ObjectResult(Result.OK, true, c1);
	}
	
	@Override
	@Transactional
	public ObjectResult queryCategory2ById(int id){
		Category2 c2 = category2DA.getCategory2ById(id);
		if (c2 == null)
			return new ObjectResult("cannot find Category2 by id "+ id, false, null);
		hibernateInitialCategory2(c2);
		return new ObjectResult(Result.OK, true, c2);
	}
	
	@Override
	@Transactional
	public ObjectResult queryDishConfigGroupById(int id){
		DishConfigGroup group = dishConfigGroupDA.getDishConfigGroupById(id);
		if (group == null)
			return new ObjectResult("cannot find DishConfigGroup by id "+ id, false, null);
		hibernateInitialConfigGroup(group);
		return new ObjectResult(Result.OK, true, group);
	}
	
	@Override
	@Transactional
	public ObjectResult queryDishConfigById(int id){
		DishConfig dc = dishConfigDA.getDishConfigById(id);
		if (dc == null)
			return new ObjectResult("cannot find DishConfig by id "+ id, false, null);
		
		return new ObjectResult(Result.OK, true, dc);
	}
	
	@Override
	@Transactional
	public ObjectListResult queryAllMenu() {
		List<Category1> c1s = category1DA.getAllCategory1();
		hibernateInitialCategory1(c1s);
		return new ObjectListResult(Result.OK,true, c1s);
	}

	@Override
	@Transactional
	public ObjectResult deleteFlavor(int userId, int flavorId){
		Flavor f = flavorDA.getFlavorById(flavorId);
		if (f == null)
			return new ObjectResult("not found Flavor by id" + flavorId, false);
		flavorDA.delete(f);
		
		UserData selfUser = userDA.getUserById(userId);
		logService.write(selfUser, LogData.LogType.FLAVOR_CHANGE.toString(),
				"User " + selfUser + " delete flavor :  " + f);
		
		return new ObjectResult(Result.OK, true);
	}
	@Override
	@Transactional
	public ObjectResult deleteCategory1(int userId, int category1Id) {
		Category1 c1 = category1DA.getCategory1ById(category1Id);
		if (c1 == null)
			return new ObjectResult("not found Category1 by id "+ category1Id, false);
		if (c1.getCategory2s() != null && !c1.getCategory2s().isEmpty())
			return new ObjectResult("this category is not empty", false);
		category1DA.delete(c1);

		//add record to menu_version
		MenuVersion mv = new MenuVersion();
		mv.setObjectId(c1.getId());
		mv.setType(ConstantValue.MENUCHANGE_TYPE_CATEGORY1DELETE);
		menuVersionDA.save(mv);
		
		// write log.
		UserData selfUser = userDA.getUserById(userId);
		logService.write(selfUser, LogData.LogType.CATEGORY1_CHANGE.toString(),
				"User " + selfUser + " delete Category1 " + c1);

		return new ObjectResult(Result.OK, true);	
	}

	@Override
	@Transactional
	public ObjectResult deleteCategory2(int userId, int category2Id) {
		Category2 c2 = category2DA.getCategory2ById(category2Id);
		if (c2 == null)
			return new ObjectResult("not found Category2 by id "+ category2Id, false);
		if (c2.getDishes() != null && !c2.getDishes().isEmpty())
			return new ObjectResult("this category is not empty", false);
		//must delete first from C1's children, otherwise report hibernate exception:
		//deleted object would be re-saved by cascade (remove deleted object from associations)
		c2.getCategory1().getCategory2s().remove(c2);
		category2DA.delete(c2);

		//add record to menu_version
		MenuVersion mv = new MenuVersion();
		mv.setObjectId(c2.getId());
		mv.setType(ConstantValue.MENUCHANGE_TYPE_CATEGORY2DELETE);
		menuVersionDA.save(mv);
		
		// write log.
		UserData selfUser = userDA.getUserById(userId);
		logService.write(selfUser, LogData.LogType.CATEGORY2_CHANGE.toString(),
				"User " + selfUser + " delete Category2 " + c2.getFirstLanguageName() + ".");

		return new ObjectResult(Result.OK, true);		
	}

	@Override
	@Transactional
	public ObjectResult deleteDish(int userId, int dishId) {
		Dish dish = dishDA.getDishById(dishId);
		if (dish == null)
			return new ObjectResult("not found dish by ID " + dishId, false);
		//must delete first from C2's children, otherwise report hibernate exception:
		//deleted object would be re-saved by cascade (remove deleted object from associations)
		dish.getCategory2().getDishes().remove(dish);
		
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
		
		//add record to menu_version
		MenuVersion mv = new MenuVersion();
		mv.setObjectId(dish.getId());
		mv.setType(ConstantValue.MENUCHANGE_TYPE_DISHDELETE);
		menuVersionDA.save(mv);
		
		// write log.
		UserData selfUser = userDA.getUserById(userId);
		logService.write(selfUser, LogData.LogType.DISH_CHANGE.toString(),
				"User " + selfUser + " delete Dish " + dish + ".");

		return new ObjectResult(Result.OK, true);
	}

	@Override
	@Transactional
	public ObjectResult updateFlavor(int userId, int id, String firstLanguageName, String secondLanguageName) {
		Flavor f = flavorDA.getFlavorById(id);
		if (f == null)
			return new ObjectResult("not found Flavor by id "+ id, false);
		f.setFirstLanguageName(firstLanguageName);
		f.setSecondLanguageName(secondLanguageName);
		flavorDA.save(f);
		
		UserData selfUser = userDA.getUserById(userId);
		logService.write(selfUser, LogData.LogType.FLAVOR_CHANGE.toString(),
				"User " + selfUser + " update flavor, id = " + id 
				+ ", firstLanguageName = " + firstLanguageName + ", secondLanguageName = " +secondLanguageName);
		
		return new ObjectResult(Result.OK, true, f);
	}
	@Override
	@Transactional
	public ObjectResult updateCategory1(int userId, int id, String firstLanguageName, String secondLanguageName, int sequence) {
		Category1 c1 = category1DA.getCategory1ById(id);
		if (c1 == null)
			return new ObjectResult("not found Category1 by id "+ id, false, null);
		c1.setFirstLanguageName(firstLanguageName);
		c1.setSecondLanguageName(secondLanguageName);
		c1.setSequence(sequence);
		category1DA.save(c1);
		hibernateInitialCategory1(c1);

		//add record to menu_version
		MenuVersion mv = new MenuVersion();
		mv.setObjectId(c1.getId());
		mv.setType(ConstantValue.MENUCHANGE_TYPE_CATEGORY1UPDATE);
		menuVersionDA.save(mv);
		
		// write log.
		UserData selfUser = userDA.getUserById(userId);
		logService.write(selfUser, LogData.LogType.CATEGORY1_CHANGE.toString(),
				"User " + selfUser + " update Category1, id = " + id 
				+ ", firstLanguageName = " + firstLanguageName + ", secondLanguageName = "
				+secondLanguageName + ", sequence = "+sequence+".");
		return new ObjectResult(Result.OK, true, c1);
	}

	@Override
	@Transactional
	public ObjectResult updateCategory2(int userId, int id, String firstLanguageName, String secondLanguageName, int sequence, 
			int category1Id, JSONArray jaPrinter) {
		Category1 c1 = category1DA.getCategory1ById(category1Id);
		if (c1 == null)
			return new ObjectResult("not found Category1 by id "+ category1Id, false, null);
		Category2 c2 = category2DA.getCategory2ById(id);
		if (c2 == null)
			return new ObjectResult("not found Category2 by id "+ id, false, null);
		//delete all relations with printer and rebuild the relation
		List<Category2Printer> cps = c2.getCategory2PrinterList();
		c2.setCategory2PrinterList(null);
		for(Category2Printer cp : cps){
			category2PrinterDA.delete(cp);
		}
		for (int i = 0; i < jaPrinter.length(); i++) {
			JSONObject joPrinter = (JSONObject) jaPrinter.get(i);
			int printerId = joPrinter.getInt("printerId");
			int printStyle = joPrinter.getInt("printStyle");
			Printer p = printerDA.getPrinterById(printerId);
			if (p == null){
				return new ObjectResult("cannot find Printer by id : "+ printerId, false, null);
			}
			Category2Printer cp = new Category2Printer();
			cp.setPrinter(p);
			cp.setCategory2(c2);
			cp.setPrintStyle(printStyle);
			c2.addCategory2Printer(cp);
			category2PrinterDA.save(cp);
		}
		
		c2.setFirstLanguageName(firstLanguageName);
		c2.setSecondLanguageName(secondLanguageName);
		c2.setSequence(sequence);
		c2.setCategory1(c1);
		category2DA.save(c2);
		
		hibernateInitialCategory2(c2);

		//add record to menu_version
		MenuVersion mv = new MenuVersion();
		mv.setObjectId(c2.getId());
		mv.setType(ConstantValue.MENUCHANGE_TYPE_CATEGORY2UPDATE);
		menuVersionDA.save(mv);
		
		// write log.
		UserData selfUser = userDA.getUserById(userId);
		logService.write(selfUser, LogData.LogType.CATEGORY2_CHANGE.toString(),
				"User " + selfUser + " update Category2, id = " + id 
				+ ", firstLanguageName = " + firstLanguageName + ", secondLanguageName = "
				+secondLanguageName + ", sequence = "+sequence+", Category1 = "+ c1
				+", Category2Printer = " + c2.getCategory2PrinterList()+".");
		return new ObjectResult(Result.OK, true, c2);
	}

	@Override
	@Transactional
	public ObjectResult updateDish(int userId, int id, String firstLanguageName, String secondLanguageName, int sequence, double price, 
			boolean isNew, boolean isSpecial, byte hotLevel, String abbreviation, int category2Id,
			int chooseMode, DishChoosePopinfo popinfo, 
			boolean autoMerge, int purchaseType, boolean allowFlavor, String description_1stlang, String description_2ndlang) {
		Category2 c2 = category2DA.getCategory2ById(category2Id);
		if (c2 == null)
			return new ObjectResult("not found Category2 by id "+ category2Id, false, null);
		Dish dish = dishDA.getDishById(id);
		if (dish == null)
			return new ObjectResult("not found dish by id "+ id, false, null);
		//check data consistency
		if (chooseMode == ConstantValue.DISH_CHOOSEMODE_POPINFOCHOOSE
				|| chooseMode == ConstantValue.DISH_CHOOSEMODE_POPINFOQUIT) {
			if (popinfo == null) {
				return new ObjectResult("no pop info", false, null);
			}
		}
		//save base property
		dish.setCategory2(c2);
		dish.setFirstLanguageName(firstLanguageName);
		dish.setSecondLanguageName(secondLanguageName);
		dish.setPrice(price);
		dish.setSequence(sequence);
		dish.setNew(isNew);
		dish.setSpecial(isSpecial);
		dish.setHotLevel(hotLevel);
		dish.setAbbreviation(abbreviation);
		dish.setChooseMode(chooseMode);
		dish.setChoosePopInfo(null);
		dish.setAutoMergeWhileChoose(autoMerge);
		dish.setPurchaseType(purchaseType);
		dish.setAllowFlavor(allowFlavor);
		dish.setDescription_1stlang(description_1stlang);
		dish.setDescription_2ndlang(description_2ndlang);
		dishDA.save(dish);
		
		//add record to menu_version
		MenuVersion mv = new MenuVersion();
		mv.setObjectId(dish.getId());
		mv.setType(ConstantValue.MENUCHANGE_TYPE_DISHUPDATE);
		menuVersionDA.save(mv);
				
		//save sub property
		if (chooseMode == ConstantValue.DISH_CHOOSEMODE_POPINFOCHOOSE 
				|| chooseMode == ConstantValue.DISH_CHOOSEMODE_POPINFOQUIT){
			popinfo.setDish(dish);
			dishChoosePopinfoDA.save(popinfo);
			dish.setChoosePopInfo(popinfo);
		}
		
		// write log.
		UserData selfUser = userDA.getUserById(userId);
		logService.write(selfUser, LogData.LogType.DISH_CHANGE.toString(),
				"User " + selfUser + " update Category2, id = " + id 
				+ ", firstLanguageName = " + firstLanguageName + ", secondLanguageName = "
				+secondLanguageName + ", sequence = "+sequence+", price = " + price + ", Category2 = "+ c2+".");
		hibernateInitialDish(dish);
		return new ObjectResult(Result.OK, true, dish);		
	}
	
	/*
     * 图片缩放,w，h为缩放的目标宽度和高度
     * src为源文件目录，dest为缩放后保存目录
     * 如果图片原始尺寸小于设定尺寸, 不进行缩放 
     * 如果要保存高质量图片, 要逐级的缩减图片,而不是直接套用目标的size
     */
    public static void makeZoomImage(BufferedImage bufImg,String dest,int targetWidth,int targetHeight) throws IOException {
        
    	int type = (bufImg.getTransparency() == Transparency.OPAQUE) ? BufferedImage.TYPE_INT_RGB : BufferedImage.TYPE_INT_ARGB;
        
    	if (targetWidth > bufImg.getWidth())
    		targetWidth = bufImg.getWidth();
    	if (targetHeight > bufImg.getHeight())
    		targetHeight = bufImg.getHeight();
    	
    	int w = bufImg.getWidth(), h = bufImg.getHeight();
    	BufferedImage ret = bufImg;
    	do{
    		if (w > targetWidth){
    			w /= 2;
    			if (w <targetWidth){
    				w = targetWidth;
    			}
    		}
    		if (h > targetHeight){
    			h /= 2;
    			if (h < targetHeight){
    				h = targetHeight;
    			}
    		}
    		BufferedImage tmp = new BufferedImage(Math.max(w, 1), Math.max(h, 1), type);
    		Graphics2D g2 = tmp.createGraphics();
    		g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
    		g2.drawImage(ret, 0, 0, w, h, null);
    		g2.dispose();
    		ret = tmp;
    	} while(w != targetWidth || h != targetHeight);
    	
        
        ImageIO.write(ret,dest.substring(dest.lastIndexOf(".")+1), new File(dest)); //写入缩减后的图片
        
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
	public ObjectResult changeDishPrice(int userId, int id, double newprice) {
		Dish dish = dishDA.getDishById(id);
		if (dish == null)
			return new ObjectResult("not found dish by id "+ id, false, null);
		double oldprice = dish.getPrice();
		dish.setPrice(newprice);
		dishDA.save(dish);
		hibernateInitialDish(dish);
		
		//add record to menu_version
		MenuVersion mv = new MenuVersion();
		mv.setObjectId(dish.getId());
		mv.setType(ConstantValue.MENUCHANGE_TYPE_DISHUPDATE);
		menuVersionDA.save(mv);
				
		// write log.
		UserData selfUser = userDA.getUserById(userId);
		logService.write(selfUser, LogData.LogType.DISH_CHANGE.toString(),
				"User " + selfUser + " change dish price, id = " + id 
				+ ", old price = " + oldprice + ", new price = " + newprice +".");
		return new ObjectResult(Result.OK, true, null);		
	}

	@Override
	@Transactional
	public ObjectResult changeDishPicture(int userId, int id, MultipartFile image) {
		Dish dish = dishDA.getDishById(id);
		if (dish == null)
			return new ObjectResult("not found dish by id "+ id, false, null);
		if (image != null && image.getSize() > 0){
			//save image as a file in server harddisk
			//generate a name for this dish. name formular: category1.englishname + '-' + category2.englishname + '-' + dish.englishname
			String fileName = null;
			if (dish.getPictureName() != null){
				File file = new File(request.getSession().getServletContext().getRealPath("/")+"..\\" + ConstantValue.CATEGORY_DISHIMAGE_BIG + "/" + dish.getPictureName());
				if (file.exists())
					file.delete();
				file = new File(request.getSession().getServletContext().getRealPath("/")+"..\\" + ConstantValue.CATEGORY_DISHIMAGE_SMALL + "/" + dish.getPictureName());
				if (file.exists())
					file.delete();
				file = new File(request.getSession().getServletContext().getRealPath("/")+"..\\" + ConstantValue.CATEGORY_DISHIMAGE_ORIGIN + "/" + dish.getPictureName());
				if (file.exists())
					file.delete();
			} 

			////重新生成文件名, 避免扩展名不一致的情况
			fileName = "DISH-"+dish.getId() + "." + image.getOriginalFilename().substring(image.getOriginalFilename().lastIndexOf(".") + 1);
		
			dish.setPictureName(fileName);
			dishDA.update(dish);
			
			try {
				//generate small picture from original picture
				String fileNameBig = request.getSession().getServletContext().getRealPath("/")+"../" + ConstantValue.CATEGORY_DISHIMAGE_BIG + "/" + fileName;
				String fileNameSmall = request.getSession().getServletContext().getRealPath("/")+"../" + ConstantValue.CATEGORY_DISHIMAGE_SMALL + "/" + fileName;
				String fileNameOrigin = request.getSession().getServletContext().getRealPath("/")+"../" + ConstantValue.CATEGORY_DISHIMAGE_ORIGIN + "/" + fileName;
				makeZoomImage(image.getInputStream(), fileNameBig, ConstantValue.DISHIMAGE_WIDTH_BIG, ConstantValue.DISHIMAGE_HEIGHT_BIG);
				makeZoomImage(image.getInputStream(), fileNameSmall, ConstantValue.DISHIMAGE_WIDTH_SMALL, ConstantValue.DISHIMAGE_HEIGHT_SMALL);
				makeZoomImage(image.getInputStream(), fileNameOrigin, ConstantValue.DISHIMAGE_WIDTH_ORIGIN, ConstantValue.DISHIMAGE_HEIGHT_ORIGIN);
			} catch (IOException e) {
				logger.error(ConstantValue.DFYMDHMS.format(new Date()) + "\n");
				logger.error("Exception when create image file for dish : " + dish, e);
				e.printStackTrace();
			} 
			
			//add record to menu_version
			MenuVersion mv = new MenuVersion();
			mv.setObjectId(dish.getId());
			mv.setType(ConstantValue.MENUCHANGE_TYPE_DISHPICTURE);
			menuVersionDA.save(mv);
			
			// write log.
			UserData selfUser = userDA.getUserById(userId);
			logService.write(selfUser, LogData.LogType.DISH_CHANGE.toString(),
					"User " + selfUser + " change dish picture, id = " + id +".");
		}
		hibernateInitialDish(dish);
		return new ObjectResult(Result.OK, true, dish);
	}

	@Override
	@Transactional
	public ObjectResult changeDishSpecial(int userId, int id, boolean isSpecial) {
		Dish dish = dishDA.getDishById(id);
		if (dish == null)
			return new ObjectResult("not found dish by id "+ id, false);
		dish.setSpecial(isSpecial);
		dishDA.save(dish);
		
		//add record to menu_version
		MenuVersion mv = new MenuVersion();
		mv.setObjectId(dish.getId());
		mv.setType(ConstantValue.MENUCHANGE_TYPE_DISHUPDATE);
		menuVersionDA.save(mv);
				
		// write log.
		UserData selfUser = userDA.getUserById(userId);
		logService.write(selfUser, LogData.LogType.DISH_CHANGE.toString(),
				"User " + selfUser + " change dish special as " + isSpecial + ", name = " + dish.getSecondLanguageName());
		return new ObjectResult(Result.OK, true);	
	}
	
	@Override
	@Transactional
	public ObjectResult changeDishPromotion(int userId, int dishid, double promotionPrice) {
		Dish dish = dishDA.getDishById(dishid);
		if (dish == null)
			return new ObjectResult("not found dish by id "+ dishid, false);
		if (dish.isPromotion()){
			return new ObjectResult("This dish is currently promoted, cannot do promotion again.", false);
		}
		dish.setPromotion(true);
		dish.setOriginPrice(dish.getPrice());
		dish.setPrice(promotionPrice);
		dishDA.save(dish);
		
		MenuVersion mv = new MenuVersion();
		mv.setObjectId(dishid);
		mv.setType(ConstantValue.MENUCHANGE_TYPE_CHANGEPROMOTION);
		menuVersionDA.save(mv);
		
		hibernateInitialDish(dish);
		// write log.
		UserData selfUser = userDA.getUserById(userId);
		logService.write(selfUser, LogData.LogType.DISH_CHANGE.toString(),
				"User " + selfUser + " change dish to be promotion, name = " + dish.getSecondLanguageName());
		return new ObjectResult(Result.OK, true, dish);	
	}

	@Override
	@Transactional
	public ObjectResult cancelDishPromotion(int userId, int dishid) {
		Dish dish = dishDA.getDishById(dishid);
		if (dish == null)
			return new ObjectResult("not found dish by id "+ dishid, false);
		if (!dish.isPromotion()){
			return new ObjectResult("This dish is not in promotion currently, cannot cancel promotion.", false);
		}
		dish.setPromotion(false);
		dish.setPrice(dish.getOriginPrice());
		dishDA.save(dish);
		MenuVersion mv = new MenuVersion();
		mv.setObjectId(dishid);
		mv.setType(ConstantValue.MENUCHANGE_TYPE_CHANGEPROMOTION);
		menuVersionDA.save(mv);
		
		hibernateInitialDish(dish);
		// write log.
		UserData selfUser = userDA.getUserById(userId);
		logService.write(selfUser, LogData.LogType.DISH_CHANGE.toString(),
				"User " + selfUser + " cancel dish's promotion status, name = " + dish.getSecondLanguageName());
		return new ObjectResult(Result.OK, true, dish);	
	}
	
	@Override
	@Transactional
	public ObjectResult changeDishNewProduct(int userId, int id, boolean isNew) {
		Dish dish = dishDA.getDishById(id);
		if (dish == null)
			return new ObjectResult("not found dish by id "+ id, false);
		dish.setNew(isNew);
		dishDA.save(dish);
		
		//add record to menu_version
		MenuVersion mv = new MenuVersion();
		mv.setObjectId(dish.getId());
		mv.setType(ConstantValue.MENUCHANGE_TYPE_DISHUPDATE);
		menuVersionDA.save(mv);
				
		// write log.
		UserData selfUser = userDA.getUserById(userId);
		logService.write(selfUser, LogData.LogType.DISH_CHANGE.toString(),
				"User " + selfUser + " change dish isNew as " + isNew + ", name = "+ dish.getSecondLanguageName());
		return new ObjectResult(Result.OK, true);
	}
	
	@Override
	@Transactional
	public ObjectResult changeDishSoldOut(int userId, int id, boolean isSoldOut) {
		Dish dish = dishDA.getDishById(id);
		if (dish == null)
			return new ObjectResult("not found dish by id "+ id, false);
		dish.setSoldOut(isSoldOut);
		dishDA.save(dish);
		hibernateInitialDish(dish);
		//add record to menu_version
		MenuVersion mv = new MenuVersion();
		mv.setObjectId(id);
		mv.setType(ConstantValue.MENUCHANGE_TYPE_DISHSOLDOUT);//不管是sold out还是取消sold out, 都使用这个值
		menuVersionDA.save(mv);
		
		// write log.
		UserData selfUser = userDA.getUserById(userId);
		logService.write(selfUser, LogData.LogType.DISH_CHANGE.toString(),
				"User " + selfUser + " change dish Sold Out as " + isSoldOut + ", name = "+dish.getSecondLanguageName());
		return new ObjectResult(Result.OK, true, dish);
	}

	@Override
	@Transactional
	public Result changeDishConfigSoldout(int userId, int configId, boolean isSoldOut){
		DishConfig config = dishConfigDA.getDishConfigById(configId);
		if (config == null)
			return new ObjectResult("not found dishconfig by id "+ configId, false);
		config.setSoldOut(isSoldOut);
		MenuVersion mv = new MenuVersion();
		mv.setObjectId(configId);
		mv.setType(ConstantValue.MENUCHANGE_TYPE_DISHCONFIGSOLDOUT);//不管是sold out还是取消sold out, 都使用这个值
		menuVersionDA.save(mv);
		
		// write log.
		UserData selfUser = userDA.getUserById(userId);
		logService.write(selfUser, LogData.LogType.DISH_CHANGE.toString(),
				"User " + selfUser + " change dish config Sold Out as " + isSoldOut + ", id = "+ configId);
		return new ObjectResult(Result.OK, true, config);
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
			info.objectId = mvs.get(i).getObjectId();
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
	
	@Override
	@Transactional
	public ObjectListResult<Flavor> queryFlavor(){
		List<Flavor> mvs = flavorDA.getAllFlavor();
		return new ObjectListResult<Flavor>(Result.OK, true, mvs);
	}
	
	@Override
	@Transactional
	public ObjectListResult<DishConfigGroup> queryDishConfigGroup(){
		List<DishConfigGroup> mvs = dishConfigGroupDA.getAllDishConfigGroup();
		for (int i = 0; i < mvs.size(); i++) {
			hibernateInitialConfigGroup(mvs.get(i));
		}
		return new ObjectListResult<DishConfigGroup>(Result.OK, true, mvs);
	}

	@Override
	@Transactional
	public ObjectListResult<DishConfig> queryDishConfig(){
		List<DishConfig> mvs = dishConfigDA.getAllDishConfig();
		return new ObjectListResult<DishConfig>(Result.OK, true, mvs);
	}
	
	@Override
	@Transactional
	public ObjectResult queryDishByName(String dishName) {
		String hql = "from Dish where firstLanguageName = '" + dishName + "'";
		List<Dish> dishes = dishDA.getSession().createQuery(hql).list();
		if (dishes == null || dishes.isEmpty()){
			return new ObjectResult("Cannot find dish by name " + dishName, false);
		}
		if (dishes.size() > 1){
			return new ObjectResult("find more than one dish by name " + dishName, false);
		}
		hibernateInitialDish(dishes.get(0));
		return new ObjectResult(Result.OK, true, dishes.get(0));
	}
	

	@Override
	@Transactional
	public ObjectResult addDishConfig(int userId, String firstLanguageName, String secondLanguageName,
			int sequence, double price, int groupId) {
		DishConfigGroup group = dishConfigGroupDA.getDishConfigGroupById(groupId);
		if (group == null){
			return new ObjectResult("cannot find dish config group by id " + groupId, false);
		}
		DishConfig dc = new DishConfig();
		dc.setFirstLanguageName(firstLanguageName);
		dc.setSecondLanguageName(secondLanguageName);
		dc.setSequence(sequence);
		dc.setPrice(price);
		dc.setGroup(group);
		dishConfigDA.save(dc);
		
		//add record to menu_version
		MenuVersion mv = new MenuVersion();
		mv.setObjectId(dc.getId());
		mv.setType(ConstantValue.MENUCHANGE_TYPE_DISHCONFIGADD);
		menuVersionDA.save(mv);
		
		// write log.
		UserData selfUser = userDA.getUserById(userId);
		logService.write(selfUser, LogData.LogType.DISHCONFIG_CHANGE.toString(), "User " + selfUser + " add dishconfig : firstLanguageName = " + firstLanguageName);
		return new ObjectResult(Result.OK, true, dc);
	}


	@Override
	@Transactional
	public ObjectResult addDishConfigGroup(int userId, String firstLanguageName, String secondLanguageName,
			String uniqueName, int sequence, int requiredQuantity, boolean allowDuplicate) {
		DishConfigGroup dg = new DishConfigGroup();
		dg.setFirstLanguageName(firstLanguageName);
		dg.setSecondLanguageName(secondLanguageName);
		dg.setUniqueName(uniqueName);
		dg.setSequence(sequence);
		dg.setRequiredQuantity(requiredQuantity);
		dg.setAllowDuplicate(allowDuplicate);
		dishConfigGroupDA.save(dg);

		//add record to menu_version
		MenuVersion mv = new MenuVersion();
		mv.setObjectId(dg.getId());
		mv.setType(ConstantValue.MENUCHANGE_TYPE_DISHCONFIGGROUPADD);
		menuVersionDA.save(mv);
		
		UserData selfUser = userDA.getUserById(userId);
		logService.write(selfUser, LogData.LogType.DISHCONFIG_CHANGE.toString(), "User " + selfUser + " add dishconfiggroup : firstLanguageName = " + firstLanguageName
				+ ", uniqueName = "+uniqueName);
		return new ObjectResult(Result.OK, true, dg);
	}


	@Override
	@Transactional
	public ObjectResult updateDishConfig(int userId, int id, String firstLanguageName, String secondLanguageName,
			int sequence, double price) {
		DishConfig dc = dishConfigDA.getDishConfigById(id);
		if (dc == null){
			return new ObjectResult("cannot find dishConfig object by id "+ id, false);
		}
		dc.setFirstLanguageName(firstLanguageName);
		dc.setSecondLanguageName(secondLanguageName);
		dc.setSequence(sequence);
		dc.setPrice(price);
		dishConfigDA.save(dc);

		//add record to menu_version
		MenuVersion mv = new MenuVersion();
		mv.setObjectId(dc.getId());
		mv.setType(ConstantValue.MENUCHANGE_TYPE_DISHCONFIGUPDATE);
		menuVersionDA.save(mv);
		
		UserData selfUser = userDA.getUserById(userId);
		logService.write(selfUser, LogData.LogType.DISHCONFIG_CHANGE.toString(), "User " + selfUser + " modify dishconfig : firstLanguageName = " + firstLanguageName);
		return new ObjectResult(Result.OK, true, dc);
	}


	@Override
	@Transactional
	public ObjectResult updateDishConfigGroup(int userId, int id, String firstLanguageName, String secondLanguageName,
			String uniqueName, int sequence, int requiredQuantity, boolean allowDuplicate) {
		DishConfigGroup dg = dishConfigGroupDA.getDishConfigGroupById(id);
		if (dg == null){
			return new ObjectResult("cannot find dishConfigGroup object by id "+ id, false);
		}
		dg.setFirstLanguageName(firstLanguageName);
		dg.setSecondLanguageName(secondLanguageName);
		dg.setAllowDuplicate(allowDuplicate);
		dg.setSequence(sequence);
		dg.setUniqueName(uniqueName);
		dg.setRequiredQuantity(requiredQuantity);
		dishConfigGroupDA.save(dg);
		hibernateInitialConfigGroup(dg);

		//add record to menu_version
		MenuVersion mv = new MenuVersion();
		mv.setObjectId(dg.getId());
		mv.setType(ConstantValue.MENUCHANGE_TYPE_DISHCONFIGGROUPUPDATE);
		menuVersionDA.save(mv);
		
		UserData selfUser = userDA.getUserById(userId);
		logService.write(selfUser, LogData.LogType.DISHCONFIG_CHANGE.toString(), "User " + selfUser + " modify dishconfiggroup : firstLanguageName = " + firstLanguageName
				+ ", uniqueName = "+uniqueName);
		return new ObjectResult(Result.OK, true, dg);
	}


	@Override
	@Transactional
	public Result deleteDishConfig(int userId, int configId) {
		DishConfig dc = dishConfigDA.getDishConfigById(configId);
		if (dc == null){
			return new ObjectResult("cannot find dishConfig object by id "+ configId, false);
		}
		dishConfigDA.delete(dc);

		//add record to menu_version
		MenuVersion mv = new MenuVersion();
		mv.setObjectId(dc.getId());
		mv.setType(ConstantValue.MENUCHANGE_TYPE_DISHCONFIGDELETE);
		menuVersionDA.save(mv);
		
		UserData selfUser = userDA.getUserById(userId);
		logService.write(selfUser, LogData.LogType.DISHCONFIG_CHANGE.toString(), "User " + selfUser + " delete dishconfig : firstLanguageName = " + dc.getFirstLanguageName());
		return new ObjectResult(Result.OK, true);
	}


	@Override
	@Transactional
	public Result deleteDishConfigGroup(int userId, int configGroupId) {
		DishConfigGroup dg = dishConfigGroupDA.getDishConfigGroupById(configGroupId);
		if (dg == null){
			return new ObjectResult("cannot find dishConfigGroup object by id "+ configGroupId, false);
		}
		dishConfigGroupDA.delete(dg);

		//add record to menu_version
		MenuVersion mv = new MenuVersion();
		mv.setObjectId(dg.getId());
		mv.setType(ConstantValue.MENUCHANGE_TYPE_DISHCONFIGGROUPDELETE);
		menuVersionDA.save(mv);
		
		UserData selfUser = userDA.getUserById(userId);
		logService.write(selfUser, LogData.LogType.DISHCONFIG_CHANGE.toString(), "User " + selfUser + " delete dishconfiggroup : firstLanguageName = " + dg.getFirstLanguageName()
				+ ", uniqueName = "+ dg.getUniqueName());
		return new ObjectResult(Result.OK, true);
	}


	@Override
	@Transactional
	public ObjectResult moveinConfigGroupForDish(int userId, int dishId, int configGroupId) {
		DishConfigGroup dg = dishConfigGroupDA.getDishConfigGroupById(configGroupId);
		if (dg == null){
			return new ObjectResult("cannot find dishConfigGroup object by id "+ configGroupId, false);
		}
		
		Dish dish = dishDA.getDishById(dishId);
		if (dish == null){
			return new ObjectResult("cannot find dish object by id "+ dishId, false);
		}
		
		dish.addConfigGroup(dg);
		dish.setAutoMergeWhileChoose(false);
		hibernateInitialDish(dish);

		//add record to menu_version
		MenuVersion mv = new MenuVersion();
		mv.setObjectId(dish.getId());
		mv.setType(ConstantValue.MENUCHANGE_TYPE_DISHMOVEINCONFIGGROUP);
		menuVersionDA.save(mv);

		UserData selfUser = userDA.getUserById(userId);
		logService.write(selfUser, LogData.LogType.DISHCONFIG_CHANGE.toString(), "User " + selfUser + " bind dishConfigGroup : firstLanguageName = " + dg.getFirstLanguageName()
				+ ", uniqueName = "+ dg.getUniqueName() +" to Dish : "+dish.getFirstLanguageName());
		return new ObjectResult(Result.OK, true, dish);
	}


	@Override
	@Transactional
	public ObjectResult moveoutConfigGroupForDish(int userId, int dishId, int configGroupId) {
		DishConfigGroup dg = dishConfigGroupDA.getDishConfigGroupById(configGroupId);
		if (dg == null){
			return new ObjectResult("cannot find dishConfigGroup object by id "+ configGroupId, false);
		}
		
		Dish dish = dishDA.getDishById(dishId);
		if (dish == null){
			return new ObjectResult("cannot find dish object by id "+ dishId, false);
		}
		
		dish.getConfigGroups().remove(dg);
		hibernateInitialDish(dish);

		//add record to menu_version
		MenuVersion mv = new MenuVersion();
		mv.setObjectId(dish.getId());
		mv.setType(ConstantValue.MENUCHANGE_TYPE_DISHMOVEOUTCONFIGGROUP);
		menuVersionDA.save(mv);

		UserData selfUser = userDA.getUserById(userId);
		logService.write(selfUser, LogData.LogType.DISHCONFIG_CHANGE.toString(), "User " + selfUser + " unbind dishConfigGroup : firstLanguageName = " + dg.getFirstLanguageName()
				+ ", uniqueName = "+ dg.getUniqueName() +" from Dish : "+dish.getFirstLanguageName());
		return new ObjectResult(Result.OK, true, dish);
	}

	@Transactional
	public void hibernateInitialCategory1(Category1 c1){
		Hibernate.initialize(c1);
		if (c1.getCategory2s() != null){
			for(Category2 c2 : c1.getCategory2s()){
				hibernateInitialCategory2(c2);
			}
		}
	}
	
	@Transactional
	public void hibernateInitialCategory1(List<Category1> c1s){
		for(Category1 c1 : c1s){
			hibernateInitialCategory1(c1);
		}
	}
	
	@Transactional
	public void hibernateInitialCategory2(Category2 c2){
		Hibernate.initialize(c2);
		if (c2.getCategory2PrinterList() != null){
			for(Category2Printer cp : c2.getCategory2PrinterList()){
				Hibernate.initialize(cp);
			}
		}
		if (c2.getDishes() != null){
			for(Dish dish : c2.getDishes()){
				hibernateInitialDish(dish);
			}
		}
	}
	
	@Transactional
	public void hibernateInitialDish(Dish dish){
		Hibernate.initialize(dish);
		Hibernate.initialize(dish.getChoosePopInfo());
//		Hibernate.initialize(dish.getMaterialConsumes());
		if (dish.getConfigGroups() != null){
			for(DishConfigGroup dg : dish.getConfigGroups()){
				hibernateInitialConfigGroup(dg);
			}
		}
	}
	
	@Transactional
	public void hibernateInitialConfigGroup(DishConfigGroup dg){
		Hibernate.initialize(dg);
		Hibernate.initialize(dg.getDishConfigs());
	}
}
