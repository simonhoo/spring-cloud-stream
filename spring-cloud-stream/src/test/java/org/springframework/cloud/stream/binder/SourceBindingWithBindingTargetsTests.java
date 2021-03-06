/*
 * Copyright 2015 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.cloud.stream.binder;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.messaging.Source;
import org.springframework.cloud.stream.utils.MockBinderRegistryConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;
import org.springframework.integration.channel.PublishSubscribeChannel;
import org.springframework.integration.context.IntegrationContextUtils;
import org.springframework.integration.support.DefaultMessageBuilderFactory;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHeaders;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

/**
 * @author Marius Bogoevici
 * @author Ilayaperumal Gopinathan
 * @author Gary Russell
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = SourceBindingWithBindingTargetsTests.TestSource.class)
public class SourceBindingWithBindingTargetsTests {

	@SuppressWarnings("rawtypes")
	@Autowired
	private BinderFactory binderFactory;

	@Autowired
	private Source testSource;

	@Autowired
	@Qualifier(IntegrationContextUtils.ERROR_CHANNEL_BEAN_NAME)
	private PublishSubscribeChannel errorChannel;

	@Autowired
	private DefaultMessageBuilderFactory messageBuilderFactory;

	@SuppressWarnings("unchecked")
	@Test
	public void testSourceOutputChannelBound() {
		Binder binder = binderFactory.getBinder(null, MessageChannel.class);
		verify(binder).bindProducer(eq("testtock"), eq(this.testSource.output()),
				Mockito.<ProducerProperties>any());
		verifyNoMoreInteractions(binder);
	}

	@Test
	public void testReadOnlyContentType() {
		Message<?> message = MessageBuilder.withPayload("foo")
				.setHeader(MessageHeaders.CONTENT_TYPE, "text/plain")
				.build();
		message = this.messageBuilderFactory.withPayload("bar").copyHeaders(message.getHeaders()).build();
		assertThat(message.getHeaders().get(MessageHeaders.CONTENT_TYPE)).isNull();
	}

	@EnableBinding(Source.class)
	@EnableAutoConfiguration
	@Import(MockBinderRegistryConfiguration.class)
	@PropertySource("classpath:/org/springframework/cloud/stream/binder/source-binding-test.properties")
	public static class TestSource {

	}
}
