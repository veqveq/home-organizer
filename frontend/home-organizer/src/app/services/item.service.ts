import {Injectable} from "@angular/core";
import {catchError, map, Observable, retry, tap, throwError} from "rxjs";
import {Page} from "../models/page";
import {HttpClient, HttpErrorResponse, HttpParams} from "@angular/common/http";
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
    }).pipe(
      map(response => response.content),
      retry(2)
    )
  }

  create(dictionaryId: string, item: DictionaryItem): Observable<string> {
    return this.http.post<string>(this.ROOT_API + '/' + dictionaryId, item)
      .pipe(
        catchError(this.handleError.bind(this))
      )
  }

  delete(dictionaryId: string, itemId: string) {
    return this.http.delete(this.ROOT_API + '/' + dictionaryId + '/' + itemId)
      .pipe(
        catchError(this.handleError.bind(this))
      )
  }

  private handleError(error: HttpErrorResponse) {
    this.errorService.handle(error.error.message)
    return throwError(() => error.message)
  }
}
