/**
 * The MIT License
 *
 *   Copyright (c) 2017, Mahmoud Ben Hassine (mahmoud.benhassine@icloud.com)
 *
 *   Permission is hereby granted, free of charge, to any person obtaining a copy
 *   of this software and associated documentation files (the "Software"), to deal
 *   in the Software without restriction, including without limitation the rights
 *   to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *   copies of the Software, and to permit persons to whom the Software is
 *   furnished to do so, subject to the following conditions:
 *
 *   The above copyright notice and this permission notice shall be included in
 *   all copies or substantial portions of the Software.
 *
 *   THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *   IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *   FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *   AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *   LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *   OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 *   THE SOFTWARE.
 */
package org.easybatch.core.listener;

import org.easybatch.core.record.Batch;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

/**
 * Composite listener that delegates processing to other listeners.
 *
 * @author Mahmoud Ben Hassine (mahmoud.benhassine@icloud.com)
 */
public class CompositeRecordWriterListener implements RecordWriterListener {

    private List<RecordWriterListener> listeners;

    /**
     * Create a new {@link CompositeRecordWriterListener}.
     */
    public CompositeRecordWriterListener() {
        this(new ArrayList<RecordWriterListener>());
    }

    /**
     * Create a new {@link CompositeRecordWriterListener}.
     *
     * @param listeners delegates
     */
    public CompositeRecordWriterListener(List<RecordWriterListener> listeners) {
        this.listeners = listeners;
    }

    @Override
    public void beforeRecordWriting(Batch batch) {
        for (RecordWriterListener listener : listeners) {
            listener.beforeRecordWriting(batch);
        }
    }

    @Override
    public void afterRecordWriting(Batch batch) {
        for (ListIterator<RecordWriterListener> iterator
                = listeners.listIterator(listeners.size());
                iterator.hasPrevious();) {
            iterator.previous().afterRecordWriting(batch);
        }
    }

    @Override
    public void onRecordWritingException(Batch batch, Throwable throwable) {
        for (ListIterator<RecordWriterListener> iterator
                = listeners.listIterator(listeners.size());
                iterator.hasPrevious();) {
            iterator.previous().onRecordWritingException(batch, throwable);
        }
    }

    /**
     * Add a delegate listener.
     *
     * @param recordWriterListener to add
     */
    public void addRecordWriterListener(RecordWriterListener recordWriterListener) {
        listeners.add(recordWriterListener);
    }
}
