package com.vtc.basetube.model;

import android.graphics.Bitmap;

public class Item {
	private String nameTellco;
	private String arrayTelco;

	private String categoryId;
	private String categoryName;
	private String serviceName;
	private String serviceId;
	private String partnerValue;
	private String value;
	private String providerId;

	private String desCription;
	private String typeTh;
	private String productCode;
	private String productId;
	private String name;
	private String productName;

	private String productSale;
	private String saleValue;

	private String title;
	private String count = "0";
	private String description;

	private String amount;
	private String saleAmount;
	private String categoryLink;
	private String discount;

	private int serviceType;
	private int icon;
	private int regId;
	private int cardType = 0;
	private int otpType;

	private boolean isSubmenu = false;
	private boolean isShowName = false;

	private Bitmap ImgThumailbm;
	private boolean isCheck = false;
	private int bankType;

	public boolean isShowName() {
		return isShowName;
	}

	public void setShowName(boolean isShowName) {
		this.isShowName = isShowName;
	}

	public void setProviderId(String providerId) {
		this.providerId = providerId;
	}

	public String getProviderId() {
		return providerId;
	}

	public void setServiceId(String serviceId) {
		this.serviceId = serviceId;
	}

	public String getServiceId() {
		return serviceId;
	}

	public void setOtpType(int otpType) {
		this.otpType = otpType;
	}

	public int getOtpType() {
		return otpType;
	}

	public void setBankType(int bankType) {
		this.bankType = bankType;
	}

	public int getBankType() {
		return bankType;
	}

	public void setCardType(int cardType) {
		this.cardType = cardType;
	}

	public int getCardType() {
		return cardType;
	}

	public void setNameTellco(String nameTellco) {
		this.nameTellco = nameTellco;
	}

	public String getNameTellco() {
		return nameTellco;
	}

	public void setArrayTelco(String arrayTelco) {
		this.arrayTelco = arrayTelco;
	}

	public String getArrayTelco() {
		return arrayTelco;
	}

	public void setCategoryLink(String categoryLink) {
		this.categoryLink = categoryLink;
	}

	public String getCategoryLink() {
		return categoryLink;
	}

	public String getAmount() {
		return amount;
	}

	public void setAmount(String amount) {
		this.amount = amount;
	}

	public String getSaleAmount() {
		return saleAmount;
	}

	public void setSaleAmount(String saleAmount) {
		this.saleAmount = saleAmount;
	}

	public void setImgThumailbm(Bitmap imgThumailbm) {
		ImgThumailbm = imgThumailbm;
	}

	public Bitmap getImgThumailbm() {
		return ImgThumailbm;
	}

	public boolean isSubmenu() {
		return isSubmenu;
	}

	public void setSubmenu(boolean isSubmenu) {
		this.isSubmenu = isSubmenu;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getCount() {
		return count;
	}

	public void setCount(String count) {
		this.count = count;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public int getIcon() {
		return icon;
	}

	public void setIcon(int icon) {
		this.icon = icon;
	}

	public int getRegId() {
		return regId;
	}

	public void setRegId(int regId) {
		this.regId = regId;
	}

	public String getDiscount() {
		return discount;
	}

	public void setDiscount(String discount) {
		this.discount = discount;
	}

	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}

	public void setProductSale(String productSale) {
		this.productSale = productSale;
	}

	public String getProductSale() {
		return productSale;
	}

	public boolean isCheck() {
		return isCheck;
	}

	public void setCheck(boolean isCheck) {
		this.isCheck = isCheck;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public String getProductName() {
		return productName;
	}

	//
	// public String getLbTop() {
	// return lbTop;
	// }

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public String getPartnerValue() {
		return partnerValue;
	}

	public void setPartnerValue(String partnerValue) {
		this.partnerValue = partnerValue;
	}

	public String getServiceName() {
		return serviceName;
	}

	public void setserviceName(String serviceName) {
		this.serviceName = serviceName;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getCategoryName() {
		return categoryName;
	}

	public void setCategoryName(String categoryName) {
		this.categoryName = categoryName;
	}

	public int getServiceType() {
		return serviceType;
	}

	public void setServiceType(int serviceType) {
		this.serviceType = serviceType;
	}

	public String getCategoryId() {
		return categoryId;
	}

	public void setCategoryId(String categoryId) {
		this.categoryId = categoryId;
	}

	public void setDesCription(String desCription) {
		this.desCription = desCription;
	}

	public String getDesCription() {
		return desCription;
	}

	public String getTypeTh() {
		return typeTh;
	}

	public void setTypeTh(String typeTh) {
		this.typeTh = typeTh;
	}

	public String getProductCode() {
		return productCode;
	}

	public void setProductCode(String productCode) {
		this.productCode = productCode;
	}

	public String getProductId() {
		return productId;
	}

	public void setProductId(String productId) {
		this.productId = productId;
	}

	public String getSaleValue() {
		return saleValue;
	}

	public void setSaleValue(String saleValue) {
		this.saleValue = saleValue;
	}

}
