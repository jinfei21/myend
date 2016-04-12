angular.module('baseTableService', []).value('BaseTableService', {
        getCustomizedTable: function (scope, filter) {
            function table(scope, filter) {
                this.recordPerPageOptions = [5, 10, 25, 50, 100];
                this.selectedRecordPerPage = this.recordPerPageOptions[1];


                this.displayedDataList = scope.dataList;
                this.total = this.displayedDataList.length;
                this.currentPage = 1;
                this.startIndex = 1;
                this.endIndex = Math.min(this.selectedRecordPerPage, this.total);
                this.pageNumber = Math.ceil(this.total / this.selectedRecordPerPage);
                this.maxPageNumDisplayed = 5;
                this.query = '';

                this.updateStartIndex = function () {
                    this.startIndex = (this.currentPage - 1) * this.selectedRecordPerPage + 1;
                };
                this.updateEndIndex = function () {
                    this.endIndex = Math.min(this.startIndex + this.selectedRecordPerPage - 1, this.total);
                };
                this.updatePageNumber = function () {
                    this.pageNumber = Math.ceil(this.total / this.selectedRecordPerPage);
                };
                this.updateIndexes = function () {
                    this.updateStartIndex();
                    this.updateEndIndex();
                };

                this.getCurrentPageNumDisplay = function () {
                    var array = [];
                    if (this.total == 0) {
                        return array
                    }
                    for (var i = Math.max(this.currentPage - 2, 1); i <= Math.min(this.pageNumber, this.currentPage + 2); i++) {
                        array.push(i);
                    }
                    var maxLength = Math.min(this.maxPageNumDisplayed, this.pageNumber);
                    if (array.length < maxLength) {
                        if (array[0] === 1) {
                            for (var i = 1; i <= maxLength - array.length + 1; i++) {
                                array.push(array[array.length - 1] + 1);
                            }
                        }
                        else {
                            for (var i = 1; i <= maxLength - array.length + 1; i++) {
                                array.unshift(array[0] - 1);
                            }
                        }
                    }
                    return array;
                };
                this.getActiveLabel = function (pageNum) {
                    if (this.currentPage === pageNum) {
                        return 'active';
                    }
                };
                this.changePage = function (pageNum) {
                    this.currentPage = pageNum;
                };
                this.previousPage = function () {
                    if (this.currentPage > 1) {
                        this.currentPage = this.currentPage - 1;
                    }
                };
                this.nextPage = function () {
                    if (this.currentPage < this.pageNumber) {
                        this.currentPage = this.currentPage + 1;
                    }
                };
                this.getDisableLabelforPrevious = function () {
                    if (this.total === 0 || this.currentPage === 1) {
                        return 'disabled';
                    }
                };
                this.getDisableLabelforNext = function () {
                    if (this.total === 0 || this.currentPage === this.pageNumber) {
                        return 'disabled';
                    }
                };
                this.gotoFirstPage = function () {
                    this.currentPage = 1;
                };
                this.gotoLastPage = function () {
                    this.currentPage = this.pageNumber;
                };
                this.getSortingClass = function (name) {
                    if (this.predicate === name) {
                        if (this.reverse) {
                            return 'sorting_asc';
                        }
                        else {
                            return 'sorting_desc';
                        }
                    }
                    return 'sorting';
                };

                scope.$watch('table.currentPage', function () {
                    scope.table.updateIndexes();
                });
                scope.$watch('table.selectedRecordPerPage', function () {
                    scope.table.currentPage = 1;
                    scope.table.updateIndexes();
                    scope.table.updatePageNumber();
                });
                scope.$watch('table.total', function () {
                    if (scope.table.total == 0) {
                        scope.table.startIndex = 0;
                        scope.table.endIndex = 0;
                    } else {
                        scope.table.updateIndexes();
                        scope.table.updatePageNumber();
                    }
                });
                scope.$watch('table.query', function () {
                    scope.table.displayedDataList = filter('filter')(scope.dataList, scope.table.query);
                    scope.table.total = scope.table.displayedDataList.length;
                    scope.table.currentPage = 1;
                });
                scope.$watchCollection('[table.predicate, table.reverse]', function () {
                    scope.table.displayedDataList = filter('orderBy')(scope.table.displayedDataList, scope.table.predicate, scope.table.reverse);
                });
                scope.$watchCollection('dataList', function () {
                    scope.table.displayedDataList = scope.dataList;
                    if (scope.table.query) {
                        scope.table.displayedDataList = filter('filter')(scope.table.displayedDataList, scope.table.query);
                    }
                    if (scope.table.reverse) {
                        scope.table.displayedDataList = filter('orderBy')(scope.table.displayedDataList, scope.table.predicate, scope.table.reverse);
                    }
                    scope.table.total = scope.table.displayedDataList.length;
                });
            }

            return new table(scope, filter);
        },
        getSpecTialTable: function (scope, filter, dataList) {
            function jobInstanceTable(scope, filter, dataList) {
                this.recordPerPageOptions = [10, 25, 50, 100];
                this.selectedRecordPerPage = this.recordPerPageOptions[0];

                this.displayedDataList = dataList;
                this.total = this.displayedDataList.length;
                this.currentPage = 1;
                this.startIndex = 1;
                this.endIndex = Math.min(this.selectedRecordPerPage, this.total);
                this.pageNumber = Math.ceil(this.total / this.selectedRecordPerPage);
                this.maxPageNumDisplayed = 5;

                this.justShowSuccess = false;
                this.statusConditions = [
                    {"ID": -3, "Text": "--选择全部--"},
                    {"ID": 1, "Text": "success"},
                    {"ID": -2, "Text": "unsuccess"},
                    {"ID": -1, "Text": "fail"},
                    {"ID": 0, "Text": "init"},
                    {"ID": 2, "Text": "running"},
                    {"ID": 3, "Text": "suspend"},
                    {"ID": 4, "Text": "init_error"},
                    {"ID": 5, "Text": "wait"},
                    {"ID": 6, "Text": "ready"},
                    {"ID": 7, "Text": "timeout"}
                ];
                this.showStatus = this.statusConditions[2].ID;
                this.query = '';

                this.updateStartIndex = function () {
                    this.startIndex = (this.currentPage - 1) * this.selectedRecordPerPage + 1;
                };
                this.updateEndIndex = function () {
                    this.endIndex = Math.min(this.startIndex + this.selectedRecordPerPage - 1, this.total);
                };
                this.updatePageNumber = function () {
                    this.pageNumber = Math.ceil(this.total / this.selectedRecordPerPage);
                };
                this.updateIndexes = function () {
                    this.updateStartIndex();
                    this.updateEndIndex();
                };

                this.getCurrentPageNumDisplay = function () {
                    var array = [];
                    if (this.total == 0) {
                        return array
                    }
                    ;
                    for (var i = Math.max(this.currentPage - 2, 1); i <= Math.min(this.pageNumber, this.currentPage + 2); i++) {
                        array.push(i);
                    }
                    var maxLength = Math.min(this.maxPageNumDisplayed, this.pageNumber);
                    if (array.length < maxLength) {
                        if (array[0] === 1) {
                            for (var i = 1; i <= maxLength - array.length + 1; i++) {
                                array.push(array[array.length - 1] + 1);
                            }
                        }
                        else {
                            for (var i = 1; i <= maxLength - array.length + 1; i++) {
                                array.unshift(array[0] - 1);
                            }
                        }
                    }
                    return array;
                };
                this.getActiveLabel = function (pageNum) {
                    if (this.currentPage === pageNum) {
                        return 'active';
                    } else {
                        return '';
                    }
                };
                this.changePage = function (pageNum) {
                    this.currentPage = pageNum;
                };
                this.previousPage = function () {
                    if (this.currentPage > 1) {
                        this.currentPage = this.currentPage - 1;
                    }
                };
                this.nextPage = function () {
                    if (this.currentPage < this.pageNumber) {
                        this.currentPage = this.currentPage + 1;
                    }
                };
                this.getDisableLabelforPrevious = function () {
                    if (this.total === 0 || this.currentPage === 1) {
                        return 'disabled';
                    }
                };
                this.getDisableLabelforNext = function () {
                    if (this.total === 0 || this.currentPage === this.pageNumber) {
                        return 'disabled';
                    }
                };
                this.gotoFirstPage = function () {
                    this.currentPage = 1;
                };
                this.gotoLastPage = function () {
                    this.currentPage = this.pageNumber;
                };
                this.getSortingClass = function (name) {
                    if (this.predicate === name) {
                        if (this.reverse) {
                            return 'sorting_asc';
                        }
                        else {
                            return 'sorting_desc';
                        }
                    }
                    return 'sorting';
                };
            }

            return new jobInstanceTable(scope, filter, dataList);
        },
        getMultiHiveTableColumnTable: function (filter, dataList) {
            function jobInstanceTable(filter, dataList) {
                this.recordPerPageOptions = [10, 25, 50, 100];
                this.selectedRecordPerPage = this.recordPerPageOptions[0];

                this.displayedDataList = dataList;
                this.total = this.displayedDataList.length;
                this.currentPage = 1;
                this.startIndex = 1;
                this.endIndex = Math.min(this.selectedRecordPerPage, this.total);
                this.pageNumber = Math.ceil(this.total / this.selectedRecordPerPage);
                this.maxPageNumDisplayed = 5;

                this.query = '';

                this.updateStartIndex = function () {
                    if (this.total == 0)
                        this.startIndex = 0;
                    else
                        this.startIndex = (this.currentPage - 1) * this.selectedRecordPerPage + 1;
                };
                this.updateEndIndex = function () {
                    if (this.total == 0)
                        this.endIndex = 0;
                    else
                        this.endIndex = Math.min(this.startIndex + this.selectedRecordPerPage - 1, this.total);
                };
                this.updatePageNumber = function () {
                    this.pageNumber = Math.ceil(this.total / this.selectedRecordPerPage);
                };
                this.updateIndexes = function () {
                    this.updateStartIndex();
                    this.updateEndIndex();
                };

                this.getCurrentPageNumDisplay = function () {
                    var array = [];
                    if (this.total == 0) {
                        return array
                    }
                    for (var i = Math.max(this.currentPage - 2, 1); i <= Math.min(this.pageNumber, this.currentPage + 2); i++) {
                        array.push(i);
                    }
                    var maxLength = Math.min(this.maxPageNumDisplayed, this.pageNumber);
                    if (array.length < maxLength) {
                        if (array[0] === 1) {
                            for (var i = 1; i <= maxLength - array.length + 1; i++) {
                                array.push(array[array.length - 1] + 1);
                            }
                        }
                        else {
                            for (var i = 1; i <= maxLength - array.length + 1; i++) {
                                array.unshift(array[0] - 1);
                            }
                        }
                    }
                    return array;
                };
                this.getActiveLabel = function (pageNum) {
                    if (this.currentPage === pageNum) {
                        return 'active';
                    } else {
                        return '';
                    }
                };
                this.changePage = function (pageNum) {
                    this.currentPage = pageNum;
                    this.updateIndexes();
                };
                this.previousPage = function () {
                    if (this.currentPage > 1) {
                        this.currentPage = this.currentPage - 1;
                        this.updateIndexes();
                    }
                };
                this.nextPage = function () {
                    if (this.currentPage < this.pageNumber) {
                        this.currentPage = this.currentPage + 1;
                        this.updateIndexes();
                    }
                };
                this.getDisableLabelforPrevious = function () {
                    if (this.total === 0 || this.currentPage === 1) {
                        return 'disabled';
                    }
                };
                this.getDisableLabelforNext = function () {
                    if (this.total === 0 || this.currentPage === this.pageNumber) {
                        return 'disabled';
                    }
                };
                this.gotoFirstPage = function () {
                    this.currentPage = 1;
                    this.updateIndexes();
                };
                this.gotoLastPage = function () {
                    this.currentPage = this.pageNumber;
                    this.updateIndexes();
                };
                this.getSortingClass = function (name) {
                    if (this.predicate === name) {
                        if (this.reverse) {
                            return 'sorting_asc';
                        }
                        else {
                            return 'sorting_desc';
                        }
                    }
                    return 'sorting';
                };
                this.selectedRecordPerPageChanged = function () {
                    this.currentPage = 1;
                    this.updateIndexes();
                    this.updatePageNumber();
                };
                this.queryChanged = function () {
                    this.displayedDataList = filter('filter')(dataList, this.query);
                    this.total = this.displayedDataList.length;
                    this.currentPage = 1;
                    this.updateIndexes();
                    this.updatePageNumber();
                };
            }

            return new jobInstanceTable(filter, dataList);
        }
    });