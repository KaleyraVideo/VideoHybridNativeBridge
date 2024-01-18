/*
 * Copyright 2023 Kaleyra @ https://www.kaleyra.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.kaleyra.video_common_ui.utils

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flatMapLatest

/**
 * Flow Utils
 */
object FlowUtils {

    /**
     * Combine 3 flows
     * @receiver Flow<T1> input flow to be combined
     * @param flow1 Flow<T2> second flow to be combined
     * @param flow2 Flow<T3> third flow to be compined
     * @param transform SuspendFunction3<[@kotlin.ParameterName] T1, [@kotlin.ParameterName] T2, [@kotlin.ParameterName] T3, R> transformation for the combine process
     * @return Flow<R> the resulting combined flow
     */
    fun <T1, T2, T3, R> Flow<T1>.combine(flow1: Flow<T2>, flow2: Flow<T3>, transform: suspend (a: T1, b: T2, c: T3) -> R): Flow<R> {
        return kotlinx.coroutines.flow.combine(this, flow1, flow2, transform)
    }

    /**
     * Combine 9 flows
     * @param flow Flow<T1> first flow to be combined
     * @param flow2 Flow<T2> second flow to be combined
     * @param flow3 Flow<T3> third flow to be combined
     * @param flow4 Flow<T4> fourth flow to be combined
     * @param flow5 Flow<T5> fifth flow to be combined
     * @param flow6 Flow<T6> sixth flow to be combined
     * @param flow7 Flow<T7> seventh flow to be combined
     * @param flow8 Flow<T8> eighth flow to be combined
     * @param flow9 Flow<T9> ninth flow to be combined
     * @param transform SuspendFunction9<T1, T2, T3, T4, T5, T6, T7, T8, T9, R> transformation for the combine process
     * @return Flow<R> the resulting combined flow
     */
    inline fun <T1, T2, T3, T4, T5, T6, T7, T8, T9, R> combine(
        flow: Flow<T1>,
        flow2: Flow<T2>,
        flow3: Flow<T3>,
        flow4: Flow<T4>,
        flow5: Flow<T5>,
        flow6: Flow<T6>,
        flow7: Flow<T7>,
        flow8: Flow<T8>,
        flow9: Flow<T9>,
        crossinline transform: suspend (T1, T2, T3, T4, T5, T6, T7, T8, T9) -> R
    ): Flow<R> {
        return kotlinx.coroutines.flow.combine(
            flow,
            flow2,
            flow3,
            flow4,
            flow5,
            flow6,
            flow7,
            flow8,
            flow9
        ) { args: Array<*> ->
            @Suppress("UNCHECKED_CAST")
            transform(
                args[0] as T1,
                args[1] as T2,
                args[2] as T3,
                args[3] as T4,
                args[4] as T5,
                args[5] as T6,
                args[6] as T7,
                args[7] as T8,
                args[8] as T9
            )
        }
    }

    /**
     * Flat map latest not null
     * @receiver Flow<T> the flow to be mapped
     * @param transform SuspendFunction1<[@kotlin.ParameterName] T, Flow<R>?> transformation for the mapping process
     * @return Flow<R> the resulting mapped flow
     */
    inline fun <T, R> Flow<T>.flatMapLatestNotNull(crossinline transform: suspend (value: T) -> Flow<R>?): Flow<R> = flatMapLatest { a ->
        transform(a) ?: emptyFlow()
    }
}
