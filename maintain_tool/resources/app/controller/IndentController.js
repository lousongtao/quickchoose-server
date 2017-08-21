/*
 * File: app/controller/IndentController.js
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

Ext.define('digitalmenu.controller.IndentController', {
    extend: 'Ext.app.Controller',

    refs: [
        {
            ref: 'indentContainer',
            selector: '#indentContainer'
        },
        {
            ref: 'gridIndent',
            selector: '#indentContainer #gridIndent'
        },
        {
            ref: 'gridIndentDetail',
            selector: '#indentContainer #gridIndentDetail'
        },
        {
            ref: 'formChangeIndentDishAmount',
            selector: '#formChangeIndentDishAmount'
        },
        {
            ref: 'formAddIndentDish',
            selector: '#formAddIndentDish'
        }
    ],

    onSetTodayButtonClick: function(button, e, eOpts) {
        var comStartDate = button.up('panel').down('#startDate');
        var comStartTime = button.up('panel').down('#startTime');
        var comEndDate = button.up('panel').down('#endDate');
        var comEndTime = button.up('panel').down('#endTime');
        var today = new Date();
        var tomorrow = Ext.Date.add(today, Ext.Date.DAY, 1);

        comStartDate.setValue(today);
        comEndDate.setValue(tomorrow);
        comStartTime.setValue('4:00');
        comEndTime.setValue('4:00');
    },

    onSetYesterdayButtonClick: function(button, e, eOpts) {
        var comStartDate = button.up('panel').down('#startDate');
        var comStartTime = button.up('panel').down('#startTime');
        var comEndDate = button.up('panel').down('#endDate');
        var comEndTime = button.up('panel').down('#endTime');
        var today = new Date();
        var yesterday = Ext.Date.add(today, Ext.Date.DAY, -1);

        comStartDate.setValue(yesterday);
        comEndDate.setValue(today);
        comStartTime.setValue('4:00');
        comEndTime.setValue('4:00');
    },

    onTimeClearButtonClick: function(button, e, eOpts) {
        var comStartDate = button.up('panel').down('#startDate');
        var comStartTime = button.up('panel').down('#startTime');
        var comEndDate = button.up('panel').down('#endDate');
        var comEndTime = button.up('panel').down('#endTime');

        comStartDate.setValue(null);
        comEndDate.setValue(null);
        comStartTime.setValue(null);
        comEndTime.setValue(null);
    },

    onQueryIndentButtonClick: function(button, e, eOpts) {
        var panelQuery = button.up('#pQuery');

        var comStartDate = panelQuery.down('#startDate');
        var comStartTime = panelQuery.down('#startTime');
        var comEndDate = panelQuery.down('#endDate');
        var comEndTime = panelQuery.down('#endTime');
        var txtDeskName = panelQuery.down('#txtDeskName');
        var cbPaid = panelQuery.down('#cbPaid');
        var cbUnpaid = panelQuery.down('#cbUnpaid');
        var cbOtherStatus = panelQuery.down('#cbOtherStatus');
        var cbOrderByTime = panelQuery.down('#cbOrderByTime');
        var cbOrderByPayment = panelQuery.down('#cbOrderByPayment');
        var cbOrderByDeskname = panelQuery.down('#cbOrderByDeskname');
        var cbAutoRefresh = panelQuery.down('#cbAutoRefresh');
        var cbRefreshTime = panelQuery.down('#cbRefreshTime');
        var grid = this.getGridIndent();

        grid.store.proxy.extraParams.userId = Ext.util.Cookies.get('userId');
        grid.store.proxy.extraParams.sessionId = Ext.util.Cookies.get('sessionId');

        if (comStartDate.getValue() !== null && comStartTime.getValue() !== null){
            var starttime = comStartDate.getRawValue() + " " + comStartTime.getRawValue();
            grid.store.proxy.extraParams.starttime = starttime;
        } else {
            grid.store.proxy.extraParams.starttime = null;
        }

        if (comEndDate.getValue() !== null && comEndTime.getValue() !== null){
            var endtime = comEndDate.getRawValue() + " " + comEndTime.getRawValue();
            grid.store.proxy.extraParams.endtime = endtime;
        } else {
            grid.store.proxy.extraParams.endtime = null;
        }

        if (txtDeskName.getValue() !== null && txtDeskName.getValue().length > 0){
            grid.store.proxy.extraParams.deskname = txtDeskName.getValue();
        } else {
            grid.store.proxy.extraParams.deskname = null;
        }

        if (cbPaid.checked || cbUnpaid.checked || cbOtherStatus.checked){
            var status = "";
            if (cbPaid.checked){
                status += "Paid";
            }
            if (cbUnpaid.checked){
                status += "Unpaid";
            }
            if (cbOtherStatus.checked){
                status += "Other";
            }
            grid.store.proxy.extraParams.status = status;
        } else {
            grid.store.proxy.extraParams.status = null;
        }

        if (cbOrderByTime.checked || cbOrderByPayment.checked || cbOrderByDeskname.checked){
            var orderby = "";
            if (cbOrderByTime.checked){
                orderby += "time";
            }
            if (cbOrderByPayment.checked){
                orderby += "status";
            }
            if (cbOrderByDeskname.checked){
                orderby += "deskname";
            }
            grid.store.proxy.extraParams.orderby = orderby;
        } else {
            grid.store.proxy.extraParams.orderby = null;
        }


        if (cbAutoRefresh.checked){
            grid.store.loadPage(1);//没有这个loadPage方法, 如果前一次的查询, 被pagetoolbar翻页了, 再次查询就会沿用原来的页号, 可能导致界面无数据.
            Ext.TaskManager.stopAll();
            var task = Ext.TaskManager.start({
                run : function(){
                    grid.store.load();
                },
                interval: cbRefreshTime.getValue() * 60 * 1000
            });
        } else {
            grid.store.loadPage(1);//没有这个loadPage方法, 如果前一次的查询, 被pagetoolbar翻页了, 再次查询就会沿用原来的页号, 可能导致界面无数据.
        }

    },

    onPayIndentButtonClick: function(button, e, eOpts) {
        var grid = this.getGridIndent();
        if (grid.getSelectionModel().getSelection() === 0)
            return;
        var record = grid.getSelectionModel().getSelection()[0];
        var values = {
            userId : Ext.util.Cookies.get("userId"),
            sessionId : Ext.util.Cookies.get("sessionId"),
            id : record.get('id'),
            operatetype : 4 //pay = 4
        };

        var me =this;

        var successCallback = function(resp){
                var result = Ext.decode(resp.responseText);

                if(result.result ==='ok'){
                    Ext.Msg.alert("SUCCESS","Pay this order successfully.");
                    //refresh grid
                    grid.store.load();
                } else if (result.result ==='invalid_session'){
                    digitalmenu.getApplication().onSessionExpired();
                } else {
                    Ext.Msg.alert('Failed',"Failed to pay this order.", resp.responseText);
                }
            };

        Ext.Msg.confirm("Confirm", "You will pay the order of desk "+ record.get('deskname'),
                        function(btnId){
                            if (btnId === 'no'){
                                return;
                            }
                            Ext.Ajax.request({
                                url: "indent/operateindent",
                                params : values,
                                success : successCallback,
                                failure : function(resp){
                                        Ext.Msg.alert('Failed',"Failed to pay this order.", resp.responseText);
                                }
                            });
                        });


    },

    onCancelIndentButtonClick: function(button, e, eOpts) {
        var grid = this.getGridIndent();
        if (grid.getSelectionModel().getSelection() === 0)
            return;
        var record = grid.getSelectionModel().getSelection()[0];
        var values = {
            userId : Ext.util.Cookies.get("userId"),
            sessionId : Ext.util.Cookies.get("sessionId"),
            id : record.get('id'),
            operatetype : 3 //cancel = 3
        };

        var me =this;

        var successCallback = function(resp){
                var result = Ext.decode(resp.responseText);

                if(result.result ==='ok'){
                    Ext.Msg.alert("SUCCESS","Cancel this order successfully.");
                    //refresh grid
                    grid.store.load();
                } else if (result.result ==='invalid_session'){
                    digitalmenu.getApplication().onSessionExpired();
                } else {
                    Ext.Msg.alert('Failed',"Failed to cancel this order.", resp.responseText);
                }
            };

        Ext.Msg.confirm("Confirm", "You will cancel the order of desk "+ record.get('deskname'),
                        function(btnId){
                            if (btnId === 'no'){
                                return;
                            }
                            Ext.Ajax.request({
                                url: "indent/operateindent",
                                params : values,
                                success : successCallback,
                                failure : function(resp){
                                        Ext.Msg.alert('Failed',"Failed to cancel this order.", resp.responseText);
                                }
                            });
                        });


    },

    onPrintIndentButtonClick: function(button, e, eOpts) {
        var grid = this.getGridIndent();
        if (grid.getSelectionModel().getSelection() === 0)
            return;
        var record = grid.getSelectionModel().getSelection()[0];
        var values = {
            userId : Ext.util.Cookies.get("userId"),
            sessionId : Ext.util.Cookies.get("sessionId"),
            indentId : record.get('id')
        };

        var me =this;

        var successCallback = function(resp){
                var result = Ext.decode(resp.responseText);

                if(result.result ==='ok'){
                    Ext.Msg.alert("SUCCESS","Send print command to printer successfully.");
                } else if (result.result ==='invalid_session'){
                    digitalmenu.getApplication().onSessionExpired();
                } else {
                    Ext.Msg.alert('Failed',"Failed to send print command.", resp.responseText);
                }
            };

        Ext.Msg.confirm("Confirm", "You will print the order on desk "+ record.get('deskname'),
                        function(btnId){
                            if (btnId === 'no'){
                                return;
                            }
                            Ext.Ajax.request({
                                url: "indent/printindent",
                                params : values,
                                success : successCallback,
                                failure : function(resp){
                                        Ext.Msg.alert('Failed',"Failed to send print command.", resp.responseText);
                                }
                            });
                        });


    },

    onAddDishButtonClick: function(button, e, eOpts) {
        //check indent status first, only the UNPAID indent could do operation for detail
        var indentGrid = this.getGridIndent();
        if (indentGrid.getSelectionModel().getSelection() === 0)
            return;
        var indentRecord = indentGrid.getSelectionModel().getSelection()[0];
        if (indentRecord.get('status') !== 1){
            Ext.Msg.alert("Error","This order is not in UNPAID status. Cannot do this operation for it.");
            return;
        }

        var p = Ext.create('digitalmenu.view.AddIndentDishForm');

        p.down('#nfIndentId').setValue(indentRecord.get('id'));
        p.down('#cbCategory1').store.load();

        var win = Ext.create('Ext.window.Window',{
        	header: false,
        	modal: true,
            border: false,
        	items:[ p ]
        });

        win.show();

    },

    onDeleteDishButtonClick: function(button, e, eOpts) {
        //check indent status first, only the UNPAID indent could do operation for detail
        var indentGrid = this.getGridIndent();
        if (indentGrid.getSelectionModel().getSelection() === 0)
            return;
        var indentRecord = indentGrid.getSelectionModel().getSelection()[0];
        if (indentRecord.get('status') !== 1){
            Ext.Msg.alert("Error","This order is not in UNPAID status. Cannot do this operation for it.");
            return;
        }
        var grid = this.getGridIndentDetail();
        if (grid.getSelectionModel().getSelection() === 0)
            return;
        var record = grid.getSelectionModel().getSelection()[0];
        var values = {
            userId : Ext.util.Cookies.get("userId"),
            sessionId : Ext.util.Cookies.get("sessionId"),
            indentId : indentRecord.get('id'),
            indentDetailId : record.get('id'),
            operatetype : 2 //delete = 2
        };

        var me =this;

        var successCallback = function(resp){
                var result = Ext.decode(resp.responseText);

                if(result.result ==='ok'){
                    Ext.Msg.alert("SUCCESS","Remove this dish successfully.");
                    //refresh grid
                    grid.store.load();
                    indentGrid.store.load();
                } else if (result.result ==='invalid_session'){
                    digitalmenu.getApplication().onSessionExpired();
                } else {
                    Ext.Msg.alert('Failed',"Failed to remove this dish from order.", resp.responseText);
                }
            };

        Ext.Msg.confirm("Confirm", "You will remove dish from the order of desk "+ indentRecord.get('deskname'),
                        function(btnId){
                            if (btnId === 'no'){
                                return;
                            }
                            Ext.Ajax.request({
                                url: "indent/operateindentdetail",
                                params : values,
                                success : successCallback,
                                failure : function(resp){
                                        Ext.Msg.alert('Failed',"Failed to remove dish from this order.", resp.responseText);
                                }
                            });
                        });
    },

    onChangeAmountButtonClick: function(button, e, eOpts) {
        //check indent status first, only the UNPAID indent could do operation for detail
        var indentGrid = this.getGridIndent();
        if (indentGrid.getSelectionModel().getSelection() === 0)
            return;
        var indentRecord = indentGrid.getSelectionModel().getSelection()[0];
        if (indentRecord.get('status') !== 1){
            Ext.Msg.alert("Error","This order is not in UNPAID status. Cannot do this operation for it.");
            return;
        }

        var grid = this.getGridIndentDetail();
        if (grid.getSelectionModel().getSelection() === 0)
            return;
        var record = grid.getSelectionModel().getSelection()[0];

        var p = Ext.create('digitalmenu.view.ChangeIndentDishAmountForm');

        p.down('#nfOriginalAmount').setValue(record.get('amount'));
        p.down('#nfIndentDetailId').setValue(record.get('id'));

        var win = Ext.create('Ext.window.Window',{
            height: 153,
            width: 280,
        	header: false,
        	modal: true,
            border: false,
        	items:[ p ]
        });

        win.show();

    },

    onPrintIndentDetailButtonClick: function(button, e, eOpts) {
        var grid = this.getGridIndentDetail();
        if (grid.getSelectionModel().getSelection() === 0)
            return;
        var record = grid.getSelectionModel().getSelection()[0];

        var values = {
            userId : Ext.util.Cookies.get("userId"),
            sessionId : Ext.util.Cookies.get("sessionId"),
            indentDetailId : record.get('id')
        };

        var me =this;

        var successCallback = function(resp){
                var result = Ext.decode(resp.responseText);

                if(result.result ==='ok'){
                    Ext.Msg.alert("SUCCESS","Send print command to printer successfully.");
                } else if (result.result ==='invalid_session'){
                    digitalmenu.getApplication().onSessionExpired();
                } else {
                    Ext.Msg.alert('Failed',"Failed to send print command.", resp.responseText);
                }
            };

        Ext.Msg.confirm("Confirm", "You will print the dish "+ record.get('dishChineseName'),
                        function(btnId){
                            if (btnId === 'no'){
                                return;
                            }
                            Ext.Ajax.request({
                                url: "indent/printindentdetail",
                                params : values,
                                success : successCallback,
                                failure : function(resp){
                                        Ext.Msg.alert('Failed',"Failed to send print command.", resp.responseText);
                                }
                            });
                        });


    },

    onSaveChangeAmountButtonClick: function(button, e, eOpts) {
        var indentGrid = this.getGridIndent();
        var grid = this.getGridIndentDetail();
        var indentDetailId = button.up('form').getValues().id;
        var newAmount = button.up('form').getValues().newAmount;
        var successCallback = function(form, action){
            if(action.result.result =='ok'){
                Ext.Msg.alert("SUCCESS","Change amount successfully.");
                var win = button.up('form').up('window');
                win.close();
                grid.store.load();
                indentGrid.store.load();
            } else if (result.result == 'invalid_session'){
                digitalmenu.getApplication().onSessionExpired();
            } else {
                Ext.Msg.alert('Failed', 'failed to change amount, please do again');
            }
        };

        var failureCallback = function(resp){
            Ext.Msg.alert('Failed','failed to change amount, please do again');
        };


        Ext.Msg.confirm("Confirm", "You will change the dish amount in the order",
                        function(btnId){
                            if (btnId === 'no'){
                                return;
                            }
                            button.up('form').submit({
                                url: "indent/operateindentdetail",
                                params: {
                                    userId : Ext.util.Cookies.get('userId'),
                                    sessionId : Ext.util.Cookies.get('sessionId'),
                                    indentDetailId : indentDetailId,
                                    newAmount : newAmount,
                                    operatetype : 5 //CHANGEAMOUNT = 5
                                },
                                success : successCallback,
                                failure : function(form, action){
                                        Ext.Msg.alert('Failed',"Failed to change dish amount in this order.", action.result.result);
                                }
                            });
                        });



    },

    onSaveAddDishButtonClick: function(button, e, eOpts) {
        var grid = this.getGridIndentDetail();
        var indentGrid = this.getGridIndent();

        var successCallback = function(form, action){
            if(action.result.result ==='ok'){
                Ext.Msg.alert("SUCCESS","Add dish successfully.");
                var win = button.up('form').up('window');
                win.close();
                grid.store.load();
                indentGrid.store.load();
            } else if (action.result.result === 'invalid_session'){
                digitalmenu.getApplication().onSessionExpired();
            } else {
                Ext.Msg.alert('Failed', 'failed to add dish, please do again');
            }
        };

        var failureCallback = function(resp){
            Ext.Msg.alert('Failed','failed to add dish, please do again');
        };

        Ext.Msg.confirm("Confirm", "You will add dish into the order",
                        function(btnId){
                            if (btnId === 'no'){
                                return;
                            }
                            button.up('form').submit({
                                url: "indent/operateindentdetail",
                                params: {
                                    userId : Ext.util.Cookies.get('userId'),
                                    sessionId : Ext.util.Cookies.get('sessionId'),
                                    operatetype : 1 //ADD dish = 1
                                },
                                success : successCallback,
                                failure : function(form, action){
                                        Ext.Msg.alert('Failed',"Failed to add dish into this order.", action.result.result);
                                }
                            });
                        });

    },

    onIndentRowClick: function(dataview, record, item, index, e, eOpts) {
        var grid = this.getGridIndentDetail();

        grid.store.proxy.extraParams.userId = Ext.util.Cookies.get('userId');
        grid.store.proxy.extraParams.sessionId = Ext.util.Cookies.get('sessionId');
        grid.store.proxy.extraParams.indentId = record.get('id');

        grid.store.load();
    },

    onChangeCategory14AddDish: function(field, newValue, oldValue, eOpts) {
        var cb = this.getFormAddIndentDish().down('#cbCategory2');
        cb.setValue(null);
        cb.store.proxy.extraParams.category1Id = newValue;
        cb.store.load(function(records, operation, success){
            if (records.length > 0){
                cb.setValue(records[0]);
            }
        });

    },

    onChangeCategory24AddDish: function(field, newValue, oldValue, eOpts) {
        var cb = this.getFormAddIndentDish().down('#cbDish');
        cb.setValue(null);
        cb.store.proxy.extraParams.category2Id = newValue;
        cb.store.load(function(records, operation, success){
            if (records.length > 0){
                cb.setValue(records[0]);
            }
        });

    },

    onChangeAutoRefreshIndent: function(field, newValue, oldValue, eOpts) {
        var cbRefreshTime = this.getIndentContainer().down('#cbRefreshTime');
        var grid = this.getIndentContainer().down('#gridIndent');
        if (grid.store.proxy.extraParams.sessionId === undefined){
            return;
        }

        if (newValue){
            Ext.TaskManager.stopAll();
            var task = Ext.TaskManager.start({
                run : function(){
                    grid.store.load();
                },
                interval: cbRefreshTime.getValue() * 60 * 1000
            });
        } else {
            Ext.TaskManager.stopAll();
        }
    },

    onChangeRefreshTime: function(field, newValue, oldValue, eOpts) {
        var cbAutoRefresh = this.getIndentContainer().down('#cbAutoRefresh');
        var grid = this.getIndentContainer().down('#gridIndent');
        if (grid.store.proxy.extraParams.sessionId === undefined){
            return;
        }
        if (!cbAutoRefresh.checked){
            return;
        }
        Ext.TaskManager.stopAll();
        var task = Ext.TaskManager.start({
            run : function(){
                grid.store.load();
            },
            interval: newValue * 60 * 1000
        });

    },

    init: function(application) {
        this.control({
            "#indentContainer #btnSetToday": {
                click: this.onSetTodayButtonClick
            },
            "#indentContainer #btnSetYesterday": {
                click: this.onSetYesterdayButtonClick
            },
            "#indentContainer #btnTimeClear": {
                click: this.onTimeClearButtonClick
            },
            "#indentContainer #btnQuery": {
                click: this.onQueryIndentButtonClick
            },
            "#indentContainer #btnPayIndent": {
                click: this.onPayIndentButtonClick
            },
            "#indentContainer #btnCancelIndent": {
                click: this.onCancelIndentButtonClick
            },
            "#indentContainer #btnPrintIndent": {
                click: this.onPrintIndentButtonClick
            },
            "#indentContainer #btnAddDish": {
                click: this.onAddDishButtonClick
            },
            "#indentContainer #btnDeleteDish": {
                click: this.onDeleteDishButtonClick
            },
            "#indentContainer #btnChangeAmount": {
                click: this.onChangeAmountButtonClick
            },
            "#indentContainer #btnPrintIndentDetail": {
                click: this.onPrintIndentDetailButtonClick
            },
            "#formChangeIndentDishAmount #btnSave": {
                click: this.onSaveChangeAmountButtonClick
            },
            "#formAddIndentDish #btnSave": {
                click: this.onSaveAddDishButtonClick
            },
            "#indentContainer #gridIndent": {
                itemclick: this.onIndentRowClick
            },
            "#formAddIndentDish #cbCategory1": {
                change: this.onChangeCategory14AddDish
            },
            "#formAddIndentDish #cbCategory2": {
                change: this.onChangeCategory24AddDish
            },
            "#indentContainer #cbAutoRefresh": {
                change: this.onChangeAutoRefreshIndent
            },
            "#indentContainer #cbRefreshTime": {
                change: this.onChangeRefreshTime
            }
        });
    }

});
