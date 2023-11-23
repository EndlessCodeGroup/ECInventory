// @ts-check

import {themes as prismThemes} from 'prism-react-renderer';

const defaultLocale = 'en';

/** @type {import('@docusaurus/types').Config} */
const config = {
  title: 'ECInventory',
  tagline: 'Change inventories how you need',
  favicon: 'img/favicon.ico',

  url: 'https://endlesscodegroup.github.io',
  baseUrl: '/ECInventory/',
  trailingSlash: false,

  // GitHub pages deployment config.
  organizationName: 'EndlessCodeGroup',
  projectName: 'ECInventory',

  onBrokenLinks: 'throw',
  onBrokenMarkdownLinks: 'warn',

  i18n: {
    defaultLocale: defaultLocale,
    locales: ['en', 'ru'],
  },

  presets: [
    [
      'classic',
      /** @type {import('@docusaurus/preset-classic').Options} */
      ({
        docs: {
          sidebarPath: require.resolve('./sidebars.js'),
          editUrl: ({locale, versionDocsDirPath, docPath}) => {
            if (locale !== defaultLocale) {
              return `https://crowdin.com/project/ecinventory/${locale}`;
            }
            return `https://github.com/EndlessCodeGroup/ECInventory/tree/develop/website/${versionDocsDirPath}/${docPath}`;
          },
        },
        theme: {
          customCss: require.resolve('./src/css/custom.css'),
        },
      }),
    ],
  ],

  themeConfig:
    /** @type {import('@docusaurus/preset-classic').ThemeConfig} */
    ({
      navbar: {
        title: 'ECInventory',
        logo: {
          alt: 'ECInventory Logo',
          src: 'img/logo.svg',
        },
        items: [
          {
            type: 'doc',
            docId: 'intro',
            position: 'left',
            label: 'Documentation',
          },
          {
            type: 'localeDropdown',
            position: 'right',
          },
          {
            href: 'https://github.com/EndlessCodeGroup/ECInventory',
            label: 'GitHub',
            position: 'right',
          },
        ],
      },
      footer: {
        style: 'dark',
        links: [
          {
            title: 'Community',
            items: [
              {
                label: 'Discord',
                href: 'https://discord.gg/5NfPsgb',
              },
            ],
          },
          {
            title: 'More',
            items: [
              {
                label: 'Translations',
                href: 'https://crowdin.com/project/ecinventory',
              },
              {
                label: 'GitHub',
                href: 'https://github.com/EndlessCodeGroup/ECInventory',
              },
            ],
          },
        ],
        copyright: `Copyright © ${new Date().getFullYear()} EndlessCode Group. Built with Docusaurus.`,
      },
      prism: {
        theme: prismThemes.github,
        darkTheme: prismThemes.dracula,
      },
    }),

 // https://docusaurus.io/docs/migration/v3#turn-off-mdx-v1-compat
  markdown: {
    mdx1Compat: {
      comments: false,
      admonitions: false,
      headingIds: true,
    },
  },
};

module.exports = config;
