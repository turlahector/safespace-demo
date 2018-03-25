// Get the CSV and create the chart
/*$.ajax({
    url: 'https://cdn.rawgit.com/highcharts/highcharts/057b672172ccc6c08fe7dbb27fc17ebca3f5b770/samples/data/analytics.csv',
    success: function (csv) {
        Highcharts.chart('exchage-graph', {

            data: {
                csv: csv.replace(/\n\n/g, '\n')
            },

            title: {
                text: 'Decentralize Exchange - Safe Space'
            },
      

            xAxis: {
                tickInterval: 7 * 24 * 3600 * 1000, // one week
                tickWidth: 0,
                gridLineWidth: 1,
                labels: {
                    align: 'left',
                    x: 3,
                    y: -3
                }
            },

            yAxis: [{ // left y axis
                title: {
                    text: null
                },
                labels: {
                    align: 'left',
                    x: 3,
                    y: 16,
                    format: '{value:.,0f}'
                },
                showFirstLabel: false
            }, { // right y axis
                linkedTo: 0,
                gridLineWidth: 0,
                opposite: true,
                title: {
                    text: null
                },
                labels: {
                    align: 'right',
                    x: -3,
                    y: 16,
                    format: '{value:.,0f}'
                },
                showFirstLabel: false
            }],

            legend: {
                align: 'left',
                verticalAlign: 'top',
                borderWidth: 0
            },

            tooltip: {
                shared: true,
                crosshairs: true
            },

            plotOptions: {
                series: {
                    cursor: 'pointer',
                    point: {
                        events: {
                            click: function (e) {
                                hs.htmlExpand(null, {
                                    pageOrigin: {
                                        x: e.pageX || e.clientX,
                                        y: e.pageY || e.clientY
                                    },
                                    headingText: this.series.name,
                                    maincontentText: Highcharts.dateFormat('%A, %b %e, %Y', this.x) + ':<br/> ' +
                                        this.y + ' sessions',
                                    width: 200
                                });
                            }
                        }
                    },
                    marker: {
                        lineWidth: 1
                    }
                }
            },

            series: [{
                name: 'All sessions',
                lineWidth: 4,
                marker: {
                    radius: 4
                }
            }, {
                name: 'New users'
            }]
        });
    }
});*/


$.ajax({
    url: jQuery("#exchangeGraphUrl").val(),
    success: function (jsonRequest) {
	            var timeStampDate = [];
	            var allAverage = [];
	            for(var i = 0; i < jsonRequest._embedded.records.length; i++){
	            	var currAvg=[];
	              currAvg.push(parseFloat(jsonRequest._embedded.records[i].timestamp));
	              currAvg.push(parseFloat(jsonRequest._embedded.records[i].avg));
	              allAverage.push(currAvg);
	            }
	            /*var xAxis = {
	               categories: timeStampDate
	            };
	            var yAxis =  [{ // left y axis
	                title: {
	                    text: null
	                },
	                labels: {
	                    align: 'left',
	                    x: 3,
	                    y: 16,
	                    format: '{value:.,0f}'
	                },
	                showFirstLabel: false,
	                min : Math.min.apply(Math,allAverage),
	                max : Math.max.apply(Math,allAverage)
	            }, { // right y axis
	                linkedTo: 0,
	                gridLineWidth: 0,
	                opposite: true,
	                title: {
	                    text: null
	                },
	                labels: {
	                    align: 'right',
	                    x: -3,
	                    y: 16,
	                    format: '{value:.,0f}'
	                },
	                showFirstLabel: false
	            }];*/
	              var json = {};
	              json.title = {
	                text: 'Decentralize Exchange - Safe Space'
	            };
	            json.xAxis = {gapGridLineWidth: 0};
	            //json.yAxis = yAxis;
	            json.series = [{ 
	            	"name":"test", 
	            	"data": allAverage.sort(),
	            	type: 'area'
			    }];
	            json.tooltip = {
	                shared: true,
	                crosshairs: true
	            }
	            json.legend = {
	                align: 'left',
	                verticalAlign: 'top',
	                borderWidth: 0
	            };
	            json.lang =  {
	                noData: "No Data Found!"
	            };
	            json.noData = {
	                style: {
	                    fontWeight: 'bold',
	                    fontSize: '15px',
	                    color: '#303030'
	                }
	            };
	            json.rangeSelector = {
	                buttons: [{
	                    type: 'hour',
	                    count: 1,
	                    text: '1h'
	                }, {
	                    type: 'day',
	                    count: 1,
	                    text: '1D'
	                }, {
	                    type: 'all',
	                    count: 1,
	                    text: 'All'
	                }],
	                selected: 1,
	                inputEnabled: false
	            };
	          Highcharts.setOptions({lang: {noData: "No Data Found"}});
              Highcharts.stockChart('exchage-graph',json);
            }
    });