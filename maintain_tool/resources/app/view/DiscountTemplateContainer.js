/*
 * File: app/view/DiscountTemplateContainer.js
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

Ext.define('digitalmenu.view.DiscountTemplateContainer', {
    extend: 'Ext.container.Container',

    requires: [
        'Ext.grid.Panel',
        'Ext.grid.View',
        'Ext.grid.column.Action',
        'Ext.form.Panel',
        'Ext.form.field.Number',
        'Ext.button.Button'
    ],

    itemId: 'discountTempListContainer',
    padding: 5,
    layout: 'border',

    initComponent: function() {
        var me = this;

        Ext.applyIf(me, {
            items: [
                {
                    xtype: 'gridpanel',
                    region: 'center',
                    frame: true,
                    itemId: 'listGrid',
                    margin: '5 0 0 0',
                    title: 'Discount Template List',
                    columnLines: true,
                    store: 'DiscountTemplateStore',
                    viewConfig: {
                        frame: false,
                        itemId: 'printerListGridView'
                    },
                    columns: [
                        {
                            xtype: 'gridcolumn',
                            draggable: false,
                            frame: false,
                            dataIndex: 'id',
                            menuDisabled: true,
                            text: 'ID'
                        },
                        {
                            xtype: 'gridcolumn',
                            draggable: false,
                            width: 150,
                            sortable: true,
                            dataIndex: 'name',
                            menuDisabled: true,
                            text: 'Name'
                        },
                        {
                            xtype: 'gridcolumn',
                            draggable: false,
                            width: 150,
                            sortable: true,
                            dataIndex: 'rate',
                            menuDisabled: true,
                            text: 'Discount'
                        },
                        {
                            xtype: 'actioncolumn',
                            items: [
                                {
                                    handler: function(view, rowIndex, colIndex, item, e, record, row) {
                                        Ext.MessageBox.confirm(
                                        "Confirm",
                                        "Do you want to delete this discount template : " + record.data.name + "?",
                                        function(btnId) {
                                            if (btnId != 'yes')
                                            return;

                                            var values = {
                                                userId: Ext.util.Cookies.get("userId"),
                                                id: record.data.id
                                            };

                                            var successCallback = function(resp, ops) {
                                                var result = Ext.decode(resp.responseText);

                                                if (result.result == 'ok') {
                                                    Ext.Msg.alert("Done","Delete discount template successfully");
                                                    view.store.load();
                                                } else {
                                                    Ext.Msg.alert("Failed to delete", result.result);
                                                }
                                            };

                                            var failureCallback = function(resp, ops) {
                                                Ext.Msg.alert("Failed to delete", resp);
                                            };

                                            Ext.Ajax.request({
                                                url: "common/deletediscounttemplate",
                                                params: values,
                                                success: successCallback,
                                                failure: failureCallback
                                            });
                                        });
                                    },
                                    icon: 'images/icon_delete.png',
                                    tooltip: 'delete desk'
                                }
                            ]
                        }
                    ]
                },
                {
                    xtype: 'form',
                    region: 'south',
                    height: 200,
                    layout: 'auto',
                    bodyPadding: 10,
                    title: '',
                    items: [
                        {
                            xtype: 'textfield',
                            itemId: 'tfName',
                            width: 400,
                            fieldLabel: 'Name',
                            name: 'name',
                            allowBlank: false
                        },
                        {
                            xtype: 'numberfield',
                            itemId: 'nfRate',
                            width: 400,
                            fieldLabel: 'Discount Rate',
                            name: 'rate',
                            value: 1
                        },
                        {
                            xtype: 'button',
                            itemId: 'btnAdd',
                            margin: 10,
                            width: 100,
                            text: 'Add'
                        },
                        {
                            xtype: 'button',
                            itemId: 'btnCancel',
                            width: 100,
                            text: 'Cancel'
                        }
                    ]
                }
            ]
        });

        me.callParent(arguments);
    }

});