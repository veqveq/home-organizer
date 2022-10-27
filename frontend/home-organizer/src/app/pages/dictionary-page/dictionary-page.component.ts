import {Component, OnInit} from '@angular/core';
import {ActivatedRoute, Params} from "@angular/router";
import {DictionaryService} from "../../services/dictionary.service";
import {Dictionary} from "../../models/dictionary";
import {ItemService} from "../../services/item.service";
import {DictionaryItem} from "../../models/dictionary-item";
import {DictionaryField} from "../../models/dictionary-field";
import {ItemFilter} from "../../models/item-filter";
import {TSMap} from "typescript-map";

@Component({
  selector: 'app-dictionary-page',
  templateUrl: './dictionary-page.component.html'
})
export class DictionaryPageComponent implements OnInit {
  dictionary: Dictionary

  filter: ItemFilter = new class implements ItemFilter {
    commonFilter: string = '';
    fieldFilters: TSMap<string, any> = new TSMap();
  }

  sort: TSMap<string, string> = new TSMap<string, string>()

  items: DictionaryItem[]

  constructor(
    private route: ActivatedRoute,
    private dictionaryService: DictionaryService,
    private itemService: ItemService
  ) {
  }

  ngOnInit(): void {
    this.route.params.subscribe((params: Params) => {
      this.dictionaryService.getById(params.id).subscribe(dic => {
        this.dictionary = dic
        this.itemService.filter(params.id, this.filter, this.getSortParam()).subscribe(items => this.items = items)
      })
    })
  }

  public getValue(fld: DictionaryField, item: DictionaryItem): any {
    if (fld.id) {
      return JSON.parse(JSON.stringify(item.fieldValues))[fld.id]
    }
  }

  delete(itemId: string) {
    if (this.dictionary.id) {
      const ID = this.dictionary.id
      this.itemService.delete(ID, itemId)
        .subscribe(() => this.itemService.filter(ID, this.filter, this.getSortParam()).subscribe(items => this.items = items))
    }
  }

  doFilter() {
    if (this.dictionary.id != null) {
      this.itemService.filter(this.dictionary.id, this.filter, this.getSortParam()).subscribe(items => this.items = items)
    }
  }

  public changeSort(fieldId: string) {
    let sort = this.getSort(fieldId)
    if (sort != null) {
      if (sort == 'ASC') {
        this.sort.set(fieldId, 'DESC')
      } else if (sort == 'DESC') {
        this.sort.delete(fieldId)
      }
    } else {
      this.sort.set(fieldId, 'ASC')
    }
    if (this.dictionary.id) {
      const ID = this.dictionary.id
      this.itemService.filter(ID, this.filter, this.getSortParam()).subscribe(items => this.items = items)
    }
  }

  public getSort(fieldId: string): string {
    return JSON.parse(JSON.stringify(this.sort))[fieldId] as string
  }

  public getSortParam(): string[] {
    if (this.sort.size() == 0) {
      return []
    }
    let sort: string[] = []
    this.sort.forEach((v, k) => sort.push(k + ',' + v))
    return sort
  }

  public getSortElement(fieldId: string): string {
    let direction = this.getSort(fieldId)
    let number = this.sort.keys().indexOf(fieldId) + 1
    if (direction) {
      return number + (direction == 'ASC' ? '&#8593;' : '&#8595;')
    }
    return ''
  }
}
