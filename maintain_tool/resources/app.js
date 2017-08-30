/*
 * File: app.js
 *
 * This file was generated by Sencha Architect version 3.0.4.
 * http://www.sencha.com/products/architect/
 *
 * This file requires use of the Ext JS 4.2.x library, under independent license.
 * License of Sencha Architect does not include license for Ext JS 4.2.x. For more
 * details see http://www.sencha.com/license or contact license@sencha.com.
 *
 * This file will be auto-generated each and everytime you save your project.
 *
 * Do NOT hand edit this file.
 */

// @require @packageOverrides
Ext.Loader.setConfig({
    enabled: true
});


Ext.application({
    stores: [
        'AccountListStore',
        'PermissionStore',
        'CategoryTreeStore',
        'MenuTreeStore',
        'Category1Store',
        'Category2Store',
        'DeskListStore',
        'IndentStore',
        'IndentDetailStore',
        'HotLevelLocalStore',
        'PrinterListStore',
        'PrinteStyleLocalStore',
        'DishStore',
        'LogListStore',
        'LogTypeLocalStore',
        'IndentRefreshTimeLocalStore',
        'ShiftWorkListStore'
    ],
    views: [
        'LoginView',
        'MainView',
        'AccountListContainer',
        'MenuContainer',
        'AddUpdateAccountForm',
        'ChangeDishPriceForm',
        'ChangeConfirmCodeForm',
        'DeskListContainer',
        'IndentContainer',
        'PrinterListContainer',
        'ChangeIndentDishAmountForm',
        'AddIndentDishForm',
        'ChangeDishPictureForm',
        'LogListContainer',
        'ChangeUserPasswordForm',
        'DeskCell',
        'DeskManageContainer',
        'ShiftWorkListContainer',
        'IndentCheckoutForm'
    ],
    controllers: [
        'AccountController',
        'MenuController',
        'IndentController',
        'MainController',
        'MaintainController',
        'QueryController',
        'DeskManageController'
    ],
    name: 'digitalmenu',

    onSessionExpired: function() {
        Ext.Msg.alert(
        '提示',
        '会话超时，请重新登录!',
        function(btn, text) {
          if (btn == 'ok') {
            Ext.util.Cookies.clear("userId");
            Ext.util.Cookies.clear("sessionId");
            var redirect = 'app';
            window.location = redirect;
          }
        });

    },

    launch: function() {
        Ext.create('digitalmenu.view.LoginView');
    }

});
