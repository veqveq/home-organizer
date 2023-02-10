import {Injectable} from '@angular/core';
import {HttpClient, HttpErrorResponse, HttpParams} from "@angular/common/http";
import {ErrorService} from "../../core/services/error.service";
import {catchError, Observable, retry, throwError} from "rxjs";
import {Page} from "../../core/models/page";
import {RecipeCompFilter} from "../models/recipe-comp-filter";
import {RecipeComp} from "../models/recipe-comp";

@Injectable({
  providedIn: 'root'
})
export class RecipeCompService {
  ROOT_API: string = "http://localhost:8082/api/v1"

  constructor(
    private http: HttpClient,
    private errorService: ErrorService
  ) {
  }

  doFilter(filter: RecipeCompFilter, endpoint: string, page: number): Observable<Page<any>> {
    return this.http.post<Page<any>>(this.ROOT_API + '/' + endpoint + '/filter', filter,{
      params: new HttpParams({
        fromObject: {
          page: page ? page : '',
          sort: "name,ASC"
        },
      })
    })
      .pipe(
        retry(2),
        catchError(this.handleError.bind(this))
      )
  }

  private handleError(error: HttpErrorResponse) {
    this.errorService.handle(error.error.message)
    return throwError(() => error.message)
  }
}
