package com.loy.down.ext

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.schedulers.Schedulers


/**
 *@Author loy
 *创建时间：2021/8/18 15:58
 *description:
 */
/**
 * .subscribeOn(Schedulers.io())
 * .observeOn(AndroidSchedulers.mainThread())
 */
fun <T> Observable<T>.defaultScheduler(): Observable<T> {
    return subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())

}

/**
 * subscribeOn(Schedulers.io())
 */
fun <T> Observable<T>.subscribeOnIO(): Observable<T> {
    return subscribeOn(Schedulers.io())
}

/**
 * observeOn(AndroidSchedulers.mainThread())
 */
fun <T> Observable<T>.observeOnMainThread(): Observable<T> {
    return observeOn(AndroidSchedulers.mainThread())
}

/**
 * observeOn(Schedulers.io())
 */
fun <T> Observable<T>.observeOnIO(): Observable<T> {
    return observeOn(Schedulers.io())
}