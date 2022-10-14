import {Component, OnInit} from '@angular/core';
import {ActivatedRoute, Params} from "@angular/router";
import {DictionaryService} from "../../services/dictionary.service";
import {Dictionary} from "../../models/dictionary";
import {ItemService} from "../../services/item.service";
import {DictionaryItem} from "../../models/dictionary-item";
import {DictionaryField} from "../../models/dictionary-field";

@Component({
  selector: 'app-dictionary-page',
  templateUrl: './dictionary-page.component.html'
})
export class DictionaryPageComponent implements OnInit {
  dictionary: Dictionary
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
        if (dic.id) {
          this.setItems(dic.id)
        }
      })
    })
  }

  private setItems(id: string): void {
    this.itemService.getAll(id)
      .subscribe(items => {
        this.items = items
      })
  }

  public getValue(fld: DictionaryField, item: DictionaryItem): any {
    if (fld.id){
      return  JSON.parse(JSON.stringify(item.fieldValues))[fld.id]
    }
  }
}
