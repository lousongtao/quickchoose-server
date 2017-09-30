/*
 * File: app/store/IndentStore.js
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

Ext.define('digitalmenu.store.IndentStore', {
    extend: 'Ext.data.Store',

    requires: [
        'Ext.data.proxy.Ajax',
        'Ext.data.reader.Json',
        'Ext.data.Field'
    ],

    constructor: function(cfg) {
        var me = this;
        cfg = cfg || {};
        me.callParent([Ext.apply({
            storeId: 'IndentStore',
            pageSize: 50,
            proxy: {
                type: 'ajax',
                url: 'indent/queryindent',
                reader: {
                    type: 'json',
                    root: 'data'
                }
            },
            fields: [
                {
                    name: 'id',
                    type: 'int'
                },
                {
                    name: 'deskName',
                    type: 'string'
                },
                {
                    name: 'startTime',
                    type: 'date'
                },
                {
                    name: 'endTime',
                    type: 'date'
                },
                {
                    name: 'totalPrice',
                    type: 'float'
                },
                {
                    name: 'paidPrice',
                    type: 'float'
                },
                {
                    name: 'status',
                    type: 'int'
                },
                {
                    name: 'payWay',
                    type: 'int'
                },
                {
                    name: 'dailySequence',
                    type: 'int'
                }
            ]
        }, cfg)]);
    }
});