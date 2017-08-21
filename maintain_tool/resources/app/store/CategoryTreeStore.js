/*
 * File: app/store/MenuTreeStore.js
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

Ext.define('digitalmenu.store.CategoryTreeStore', {
    extend: 'Ext.data.TreeStore',

    requires: [
        'Ext.data.Field',
        'Ext.util.Sorter',
        'Ext.data.proxy.Ajax',
        'Ext.data.reader.Json'
    ],

    constructor: function(cfg) {
        var me = this;
        cfg = cfg || {};
        me.callParent([Ext.apply({
            autoLoad: false,
            storeId: 'CategoryTreeStore',
            fields: [
                {
                    name: 'id',
                    type: 'string'
                },
                {
                    name: 'chineseName',
                    type: 'string'
                },
                {
                    name: 'englishName',
                    type: 'string'
                },
                {
                    name: 'sequence',
                    type: 'int'
                },
                {
                    name: 'price',
                    type: 'float'
                },
                {
                    name: 'parent',
                    type: 'string'
                }
            ],
            sorters: {
                property: 'sequence'
            },
            proxy: {
                type: 'ajax',
                url: 'menu/querymenu',
                reader: {
                    type: 'json',
                    root: 'infos'
                }
            }
        }, cfg)]);
    }
});