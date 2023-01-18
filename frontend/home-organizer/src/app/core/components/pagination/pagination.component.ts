import {Component, EventEmitter, Input, Output} from '@angular/core';
import {Page} from "../../models/page";

@Component({
  selector: 'app-pagination',
  templateUrl: './pagination.component.html'
})
export class PaginationComponent {
  @Input('page') page: Page<any>
  @Output() sizeChanged = new EventEmitter();
  @Output() pageChanged = new EventEmitter();

  currentPage: number = 1


  constructor() {
  }

  getPages(): number[] {
    if (this.page) {
      let result: number[] = []
      let start = this.currentPage
      let end = this.currentPage
      if (this.currentPage <= 3) {
        start = 1
        end = 5 < this.page.totalPages ? 5 : this.page.totalPages
      } else if (this.currentPage >= this.page.totalPages - 3) {
        start = this.page.totalPages - 4 > 0 ? this.page.totalPages - 4 : 0
        end = this.page.totalPages
      } else {
        result.push(1)
        result.push(-1)
        for (let i = this.currentPage - 1; i <= this.currentPage + 1; i++) {
          result.push(i)
        }
        result.push(-1)
        result.push(this.page.totalPages)
        return result
      }
      for (let i = start; i <= end; i++) {
        result.push(i)
      }
      return result;
    }
    return []
  }

  changePage(page: number) {
    this.currentPage = page
    this.pageChanged.emit(this.currentPage - 1)
  }

  changeSize(size: string) {
    this.currentPage = 1
    this.sizeChanged.emit(size)
  }

}
