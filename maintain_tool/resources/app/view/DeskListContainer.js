/*
 * File: app/view/DeskListContainer.js
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

Ext.define('digitalmenu.view.DeskListContainer', {
    extend: 'Ext.container.Container',

    requires: [
        'Ext.grid.Panel',
        'Ext.grid.View',
        'Ext.grid.column.Action',
        'Ext.form.Panel',
        'Ext.form.field.Number',
        'Ext.button.Button'
    ],

    itemId: 'deskListContainer',
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
                    itemId: 'deskListGrid',
                    margin: '5 0 0 0',
                    title: 'Desk List',
                    columnLines: true,
                    store: 'DeskListStore',
                    viewConfig: {
                        frame: false,
                        itemId: 'deskListGridView'
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
                            dataIndex: 'sequence',
                            menuDisabled: true,
                            text: 'Sequence'
                        },
                        {
                            xtype: 'actioncolumn',
                            items: [
                                {
                                    handler: function(view, rowIndex, colIndex, item, e, record, row) {
                                        Ext.MessageBox.confirm(
                                        "Confirm",
                                        "Do you want to delete desk : " + record.data.name + "?",
                                        function(btnId) {
                                            if (btnId != 'yes')
                                            return;

                                            var values = {
                                                userId: Ext.util.Cookies.get("userId"),
                                                sessionId: Ext.util.Cookies.get("sessionId"),
                                                id: record.data.id
                                            };

                                            var successCallback = function(resp, ops) {
                                                var result = Ext.decode(resp.responseText);

                                                if (result.result == 'ok') {
                                                    Ext.Msg.alert("Done","Delete desk successfully");
                                                    view.store.load();
                                                } else if (result.result == 'invalid_session') {
                                                    digitalmenu.getApplication().onSessionExpired();
                                                } else {
                                                    Ext.Msg.alert("Failed to delete", result.result);
                                                }
                                            };

                                            var failureCallback = function(resp, ops) {
                                                Ext.Msg.alert("Failed to delete", resp);
                                            };

                                            Ext.Ajax.request({
                                                url: "common/deletedesk",
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
                    height: 150,
                    bodyPadding: 10,
                    title: '',
                    items: [
                        {
                            xtype: 'textfield',
                            anchor: '100%',
                            itemId: 'txtName',
                            fieldLabel: 'Name',
                            name: 'name'
                        },
                        {
                            xtype: 'numberfield',
                            anchor: '100%',
                            itemId: 'tfSequence',
                            fieldLabel: 'Sequence',
                            name: 'sequence'
                        },
                        {
                            xtype: 'button',
                            formBind: true,
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