import {Injectable} from "@angular/core";
import {map, Observable, retry, tap} from "rxjs";
import {Page} from "../models/page";
import {HttpClient, HttpParams} from "@angular/common/http";
import {ErrorService} from "./error.service";
import {DictionaryItem} from "../models/dictionary-item";

@Injectable({
  providedIn: 'root'
})
export class ItemService {
  ROOT_API: string = "http://localhost:8081/ho/api/v1/items"

  constructor(
    private http: HttpClient,
    private errorService: ErrorService
  ) {
  }

  getAll(dictionaryId: string): Observable<DictionaryItem[]> {
    return this.http.get<Page<DictionaryItem>>(this.ROOT_API + '/' + dictionaryId, {
      params: new HttpParams({
        fromObject: {
          page: 0,
          size: 10
        }
      })
      // , responseType: 'json'
    }).pipe(
      tap(val=>console.log(val)),
      map(response => response.content),
      retry(2)
    )
  }
}
