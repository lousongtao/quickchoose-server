/*
 * File: app/view/IndentContainer.js
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

Ext.define('digitalmenu.view.IndentContainer', {
    extend: 'Ext.container.Container',

    requires: [
        'Ext.form.field.Date',
        'Ext.form.field.Time',
        'Ext.button.Button',
        'Ext.form.CheckboxGroup',
        'Ext.form.field.Checkbox',
        'Ext.grid.Panel',
        'Ext.grid.column.Template',
        'Ext.XTemplate',
        'Ext.grid.column.Number',
        'Ext.grid.column.Date',
        'Ext.grid.View',
        'Ext.selection.RowModel',
        'Ext.toolbar.Paging'
    ],

    height: 645,
    itemId: 'indentContainer',
    width: 1093,
    layout: 'border',

    initComponent: function() {
        var me = this;

        Ext.applyIf(me, {
            items: [
                {
                    xtype: 'panel',
                    region: 'north',
                    height: 130,
                    itemId: 'pQuery',
                    maxHeight: 130,
                    layout: {
                        type: 'vbox',
                        align: 'stretch',
                        padding: '10 0 0 0'
                    },
                    items: [
                        {
                            xtype: 'panel',
                            height: 30,
                            itemId: 'pTime',
                            maxHeight: 30,
                            layout: {
                                type: 'hbox',
                                align: 'stretch'
                            },
                            items: [
                                {
                                    xtype: 'panel',
                                    height: 30,
                                    itemId: 'pStartTime',
                                    width: 330,
                                    layout: {
                                        type: 'hbox',
                                        align: 'stretch'
                                    },
                                    items: [
                                        {
                                            xtype: 'datefield',
                                            itemId: 'startDate',
                                            maxHeight: 25,
                                            padding: '0 0 0 5',
                                            width: 200,
                                            fieldLabel: 'start time',
                                            labelWidth: 80
                                        },
                                        {
                                            xtype: 'timefield',
                                            height: 25,
                                            itemId: 'startTime',
                                            maxHeight: 25,
                                            width: 105,
                                            labelWidth: 0,
                                            format: 'H:i'
                                        }
                                    ]
                                },
                                {
                                    xtype: 'panel',
                                    height: 30,
                                    itemId: 'pEndTime',
                                    width: 330,
                                    layout: {
                                        type: 'hbox',
                                        align: 'stretch'
                                    },
                                    items: [
                                        {
                                            xtype: 'datefield',
                                            itemId: 'endDate',
                                            maxHeight: 25,
                                            padding: '0 0 0 5',
                                            width: 200,
                                            fieldLabel: 'end time',
                                            labelWidth: 80
                                        },
                                        {
                                            xtype: 'timefield',
                                            height: 25,
                                            itemId: 'endTime',
                                            maxHeight: 25,
                                            width: 105,
                                            labelWidth: 0,
                                            format: 'H:i'
                                        }
                                    ]
                                },
                                {
                                    xtype: 'button',
                                    flex: 1,
                                    margins: '3',
                                    height: 20,
                                    itemId: 'btnSetToday',
                                    maxHeight: 20,
                                    maxWidth: 80,
                                    text: 'Today'
                                },
                                {
                                    xtype: 'button',
                                    flex: 1,
                                    margins: '3',
                                    height: 25,
                                    itemId: 'btnSetYesterday',
                                    maxHeight: 20,
                                    maxWidth: 80,
                                    width: 50,
                                    text: 'Yesterday'
                                },
                                {
                                    xtype: 'button',
                                    flex: 1,
                                    margins: '3',
                                    height: 25,
                                    itemId: 'btnTimeClear',
                                    maxHeight: 20,
                                    maxWidth: 80,
                                    width: 50,
                                    text: 'Clear'
                                }
                            ]
                        },
                        {
                            xtype: 'panel',
                            flex: 1,
                            height: 40,
                            itemId: 'pStatus',
                            maxHeight: 40,
                            layout: {
                                type: 'hbox',
                                align: 'stretch',
                                padding: '10 0 0 0'
                            },
                            items: [
                                {
                                    xtype: 'textfield',
                                    itemId: 'txtDeskName',
                                    maxHeight: 25,
                                    padding: '0 0 0 5',
                                    fieldLabel: 'table name',
                                    labelWidth: 80
                                },
                                {
                                    xtype: 'checkboxgroup',
                                    margins: '0 0 0 50',
                                    maxHeight: 25,
                                    width: 364,
                                    fieldLabel: 'payment status',
                                    items: [
                                        {
                                            xtype: 'checkboxfield',
                                            itemId: 'cbPaid',
                                            labelWidth: 50,
                                            boxLabel: 'Paid'
                                        },
                                        {
                                            xtype: 'checkboxfield',
                                            itemId: 'cbUnpaid',
                                            boxLabel: 'Unpaid',
                                            checked: true
                                        },
                                        {
                                            xtype: 'checkboxfield',
                                            itemId: 'cbOtherStatus',
                                            boxLabel: 'Others'
                                        }
                                    ]
                                }
                            ]
                        },
                        {
                            xtype: 'panel',
                            flex: 1,
                            height: 40,
                            itemId: 'pOrderby',
                            maxHeight: 40,
                            layout: {
                                type: 'hbox',
                                align: 'stretch',
                                padding: '10 0 0 0'
                            },
                            items: [
                                {
                                    xtype: 'checkboxgroup',
                                    flex: 3,
                                    margins: '0 0 0 5',
                                    maxHeight: 25,
                                    maxWidth: 400,
                                    width: 400,
                                    fieldLabel: 'Order By',
                                    items: [
                                        {
                                            xtype: 'checkboxfield',
                                            itemId: 'cbOrderByTime',
                                            margin: 0,
                                            labelWidth: 50,
                                            boxLabel: 'time',
                                            checked: true
                                        },
                                        {
                                            xtype: 'checkboxfield',
                                            itemId: 'cbOrderByPayment',
                                            boxLabel: 'payment'
                                        },
                                        {
                                            xtype: 'checkboxfield',
                                            itemId: 'cbOrderByDeskname',
                                            boxLabel: 'table name'
                                        }
                                    ]
                                },
                                {
                                    xtype: 'button',
                                    flex: 1,
                                    margins: '0 0 0 30',
                                    itemId: 'btnQuery',
                                    maxWidth: 150,
                                    width: 150,
                                    text: 'QUERY'
                                },
                                {
                                    xtype: 'checkboxfield',
                                    flex: 1,
                                    itemId: 'cbAutoRefresh',
                                    maxWidth: 100,
                                    padding: '0 0 0 20',
                                    boxLabel: 'Auto Refresh',
                                    checked: true
                                },
                                {
                                    xtype: 'combobox',
                                    flex: 1,
                                    height: 30,
                                    itemId: 'cbRefreshTime',
                                    maxHeight: 20,
                                    maxWidth: 100,
                                    editable: false,
                                    displayField: 'name',
                                    store: 'IndentRefreshTimeLocalStore',
                                    valueField: 'id'
                                }
                            ]
                        }
                    ]
                },
                {
                    xtype: 'panel',
                    region: 'center',
                    itemId: 'pTable',
                    layout: {
                        type: 'hbox',
                        align: 'stretch'
                    },
                    items: [
                        {
                            xtype: 'gridpanel',
                            flex: 0,
                            margins: '0 5 0 0',
                            itemId: 'gridIndent',
                            maxWidth: 500,
                            minWidth: 500,
                            width: 500,
                            title: 'Order',
                            store: 'IndentStore',
                            columns: [
                                {
                                    xtype: 'gridcolumn',
                                    width: 90,
                                    dataIndex: 'deskName',
                                    text: 'Table Name'
                                },
                                {
                                    xtype: 'templatecolumn',
                                    itemId: 'status',
                                    tpl: [
                                        '<tpl if=\'status == 1\'>Unpaid',
                                        '<tpl elseif=\'status == 2\'>Closed',
                                        '<tpl elseif=\'status == 3\'>Paid',
                                        '<tpl elseif=\'status == 4\'>Canceled',
                                        '</tpl>'
                                    ],
                                    width: 100,
                                    dataIndex: 'status',
                                    text: 'Status'
                                },
                                {
                                    xtype: 'numbercolumn',
                                    width: 80,
                                    dataIndex: 'dailySequence',
                                    text: 'Sequence',
                                    format: '0'
                                },
                                {
                                    xtype: 'numbercolumn',
                                    width: 70,
                                    dataIndex: 'totalPrice',
                                    text: 'Price'
                                },
                                {
                                    xtype: 'numbercolumn',
                                    width: 70,
                                    dataIndex: 'paidPrice',
                                    text: 'Paid Price'
                                },
                                {
                                    xtype: 'datecolumn',
                                    minWidth: 150,
                                    width: 150,
                                    dataIndex: 'startTime',
                                    text: 'Start Time',
                                    format: 'Y-m-d H:i:s'
                                },
                                {
                                    xtype: 'datecolumn',
                                    minWidth: 150,
                                    width: 150,
                                    dataIndex: 'endTime',
                                    text: 'End Time',
                                    format: 'Y-m-d H:i:s'
                                },
                                {
                                    xtype: 'templatecolumn',
                                    tpl: [
                                        '<tpl if=\'status == 0\'>non',
                                        '<tpl elseif=\'status == 1\'>Cash',
                                        '<tpl elseif=\'status == 2\'>Card',
                                        '<tpl elseif=\'status == 3\'>Member',
                                        '</tpl>'
                                    ],
                                    dataIndex: 'payWay',
                                    text: 'Pay Way'
                                }
                            ],
                            selModel: Ext.create('Ext.selection.RowModel', {
                                mode: 'SINGLE'
                            }),
                            dockedItems: [
                                {
                                    xtype: 'pagingtoolbar',
                                    dock: 'bottom',
                                    width: 360,
                                    displayInfo: true,
                                    store: 'IndentStore'
                                }
                            ]
                        },
                        {
                            xtype: 'gridpanel',
                            flex: 0,
                            itemId: 'gridIndentDetail',
                            maxWidth: 600,
                            minWidth: 600,
                            width: 600,
                            title: 'Order Detail',
                            store: 'IndentDetailStore',
                            columns: [
                                {
                                    xtype: 'gridcolumn',
                                    width: 150,
                                    dataIndex: 'dishChineseName',
                                    text: 'Chinese Name'
                                },
                                {
                                    xtype: 'gridcolumn',
                                    width: 150,
                                    dataIndex: 'dishEnglishName',
                                    text: 'English Name'
                                },
                                {
                                    xtype: 'numbercolumn',
                                    width: 70,
                                    dataIndex: 'amount',
                                    text: 'Amount',
                                    format: '0'
                                },
                                {
                                    xtype: 'numbercolumn',
                                    width: 70,
                                    dataIndex: 'dishPrice',
                                    text: 'Price'
                                },
                                {
                                    xtype: 'gridcolumn',
                                    width: 150,
                                    dataIndex: 'additionalRequirements',
                                    text: 'Requirements'
                                }
                            ],
                            selModel: Ext.create('Ext.selection.RowModel', {
                                mode: 'SINGLE'
                            })
                        }
                    ]
                },
                {
                    xtype: 'panel',
                    region: 'south',
                    height: 55,
                    itemId: 'pButton',
                    padding: '',
                    items: [
                        {
                            xtype: 'button',
                            height: 40,
                            itemId: 'btnPayIndent',
                            margin: 5,
                            width: 100,
                            text: 'Pay Order'
                        },
                        {
                            xtype: 'button',
                            height: 40,
                            itemId: 'btnCancelIndent',
                            margin: 5,
                            width: 100,
                            text: 'Cancel Order'
                        },
                        {
                            xtype: 'button',
                            height: 40,
                            itemId: 'btnPrintIndent',
                            margin: 5,
                            width: 100,
                            text: 'Print Order'
                        },
                        {
                            xtype: 'button',
                            height: 40,
                            itemId: 'btnAddDish',
                            margin: '5 5 5 200',
                            width: 100,
                            text: 'Add Dish'
                        },
                        {
                            xtype: 'button',
                            height: 40,
                            itemId: 'btnDeleteDish',
                            margin: 5,
                            width: 100,
                            text: 'Delete Dish'
                        },
                        {
                            xtype: 'button',
                            height: 40,
                            itemId: 'btnChangeAmount',
                            margin: 5,
                            width: 120,
                            text: 'Change Amount'
                        },
                        {
                            xtype: 'button',
                            height: 40,
                            itemId: 'btnPrintIndentDetail',
                            margin: 5,
                            width: 120,
                            text: 'Print Dish'
                        }
                    ]
                }
            ],
            listeners: {
                removed: {
                    fn: me.onIndentContainerRemoved,
                    scope: me
                }
            }
        });

        me.callParent(arguments);
    },

    onIndentContainerRemoved: function(component, ownerCt, eOpts) {
        Ext.TaskManager.stopAll();
    }

});